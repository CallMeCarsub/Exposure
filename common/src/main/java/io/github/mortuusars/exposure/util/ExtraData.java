package io.github.mortuusars.exposure.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import io.github.mortuusars.exposure.Exposure;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Extension of CompoundTag to allow better type safety. <br>
 * {@link Type} is meant to be stored in a static final field in appropriate places.
 */
public class ExtraData extends CompoundTag {
    public static final Codec<ExtraData> CODEC = CompoundTag.CODEC.xmap(ExtraData::new, data -> data);
    public static final StreamCodec<ByteBuf, ExtraData> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(ExtraData::new, data -> data);

    public static final ExtraData EMPTY = new ExtraData(Collections.emptyMap());
    private final Map<String, Tag> tags;

    protected ExtraData(Map<String, Tag> tags) {
        super(tags);
        this.tags = tags;
    }

    public ExtraData() {
        this(new HashMap<>());
    }

    public ExtraData(CompoundTag tag) {
        this(new HashMap<>());
        merge(tag);
    }

    public <T> Optional<T> get(@NotNull ExtraData.Type<T> type) {
        if (!contains(type.key())) return Optional.empty();
        try {
            return Optional.ofNullable(type.getter().apply(this, type.key()));
        } catch (Exception e) {
            Exposure.LOGGER.error("Cannot get ExtraData entry: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public <T> T getOrDefault(@NotNull ExtraData.Type<T> type, T defaultValue) {
        if (!contains(type.key())) return defaultValue;
        try {
            @Nullable T value = type.getter().apply(this, type.key());
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            Exposure.LOGGER.error("Cannot get ExtraData entry: {}", e.getMessage());
            return defaultValue;
        }
    }

    public <T> void put(Type<T> type, @NotNull T value) {
        Preconditions.checkNotNull(value, "value");
        type.setter().accept(this, type.key(), value);
    }

    public <T> void remove(Type<T> type) {
        remove(type.key());
    }

    // --

    @Override
    public @NotNull ExtraData copy() {
        Map<String, Tag> map = Maps.newHashMap(Maps.transformValues(this.tags, Tag::copy));
        return new ExtraData(map);
    }

    @Override
    public @NotNull ExtraData merge(CompoundTag other) {
        for (String key : other.keySet()) {
            Tag tag = other.get(key);
            assert tag != null;
            if (tag.getId() == 10) {
                if (this.contains(key)) {
                    CompoundTag compoundTag = this.getCompound(key).orElseThrow();
                    compoundTag.merge((CompoundTag) tag);
                } else {
                    this.put(key, tag.copy());
                }
            } else {
                this.put(key, tag.copy());
            }
        }

        return this;
    }

    // --

    public record Type<T>(String key, BiFunction<ExtraData, String, @Nullable T> getter,
                          TriConsumer<ExtraData, String, T> setter) {
        public static Type<String> string(String key) {
            return new Type<>(key, (tag,k) -> tag.getString(k).orElseThrow(), ExtraData::putString);
        }

        public static Type<Boolean> bool(String key) {
            return new Type<>(key, (tag,k) -> tag.getBoolean(k).orElseThrow(), ExtraData::putBoolean);
        }

        public static Type<Integer> intVal(String key) {
            return new Type<>(key, (tag,k) -> tag.getInt(k).orElseThrow(), ExtraData::putInt);
        }

        public static Type<Long> longVal(String key) {
            return new Type<>(key, (tag,k) -> tag.getLong(k).orElseThrow(), ExtraData::putLong);
        }

        public static Type<Float> floatVal(String key) {
            return new Type<>(key, (tag,k) -> tag.getFloat(k).orElseThrow(), ExtraData::putFloat);
        }

        public static Type<Double> doubleVal(String key) {
            return new Type<>(key, (tag,k) -> tag.getDouble(k).orElseThrow(), ExtraData::putDouble);
        }

        public static <T extends StringRepresentable> Type<T> stringRepresentable(String key, Function<String, @Nullable T> deserializeFunction) {
            return new Type<>(key,
                    (data, k) -> deserializeFunction.apply(data.getString(k).orElseThrow()),
                    (data, k, value) -> data.putString(k, value.getSerializedName()));
        }

        public static Type<Vec3> vec3(String key) {
            return new Type<>(key,
                    (data, k) -> {
                        ListTag pos = data.getList(k).orElseThrow();
                        return new Vec3(pos.getDouble(0).orElseThrow(), pos.getDouble(1).orElseThrow(), pos.getDouble(2).orElseThrow());
                    },
                    (data, k, value) -> {
                        ListTag pos = new ListTag();
                        pos.add(DoubleTag.valueOf(value.x()));
                        pos.add(DoubleTag.valueOf(value.y()));
                        pos.add(DoubleTag.valueOf(value.z()));
                        data.put(k, pos);
                    });
        }

        public static Type<Identifier> Identifier(String key) {
            return new Type<>(key,
                    (data, k) -> Identifier.parse(data.getString(k).orElseThrow()),
                    (data, k, value) -> data.putString(k, value.toString()));
        }

        public static <T> Type<List<T>> list(String key, int tagType, Function<Tag, T> extractFunc, Function<T, Tag> packFunc) {
            return new Type<>(key,
                    (data, k) -> data.getList(k).orElseThrow().stream()
                            .map(extractFunc)
                            .toList(),
                    (data, k, value) -> {
                        ListTag list = new ListTag();
                        list.addAll(value.stream()
                                .map(packFunc)
                                .toList());
                        data.put(k, list);
                    });
        }

        public static <T> Type<List<T>> stringBasedList(String key, Function<String, T> extractFunc, Function<T, String> packFunc) {
            return list(key, Tag.TAG_STRING, tag -> extractFunc.apply(tag.asString().orElseThrow()), value -> StringTag.valueOf(packFunc.apply(value)));
        }
    }
}