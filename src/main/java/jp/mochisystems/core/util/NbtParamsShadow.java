package jp.mochisystems.core.util;

import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.*;

public class NbtParamsShadow {

    private final List<Param<?>> params = new ArrayList<>();
    private final List<Class<?>> classes = new ArrayList<>();
    private final NBTTagCompound nbt = new NBTTagCompound();
    private final NBTTagCompound src;

    public NbtParamsShadow(NBTTagCompound src)
    {
        this.src = src;
    }

    public <T> Param<T> Create(
            String key,
            Function<NBTTagCompound, BiConsumer<String, T>> set,
            BiFunction<NBTTagCompound, String, T> get)
    {
        Param<T> p = new Param<>(key, set.apply(nbt), _p -> _p.value = get.apply(src, key));
        p.value = get.apply(src, key);
        params.add(p);
        return p;
    }
    public <T> Class<T> Create(
            String key,
            T object,
            Function<T, BiConsumer<String, NBTTagCompound>> write)
    {
        Class<T> c = new Class<>(key, object, nbt, write, _p -> _p.write.apply(_p.ref).accept(key, src));
        write.apply(c.ref).accept(key, nbt);
        classes.add(c);
        return c;
    }

    public void WriteAll()
    {
        params.forEach(Param::Write);
        classes.forEach(Class::Write);
    }
    public void Reset(NBTTagCompound nbt)
    {
        src.merge(nbt);
        params.forEach(Param::Reset);
        classes.forEach(Class::Reset);
    }

    public NBTTagCompound GetNbtTag()
    {
        return nbt;
    }

    public static class Param<T>{
        private final String key;
        private T value;
        private final BiConsumer<String, T> set;
        public final Consumer<Param<T>> reset;
        private Param(String key, BiConsumer<String, T> set, Consumer<Param<T>> reset)
        {
            this.key = key;
            this.set = set;
            this.reset = reset;
        }
        public T Get() {return value;}
        public void Set(T p) {value = p;}
        void Write() {if (!"".equals(key)) set.accept(key, value);}
        public void Reset() {reset.accept(this);}
    }

    public static class Class<T>{
        private final String key;
        private T ref;
        private final NBTTagCompound nbt;
        private final Function<T, BiConsumer<String, NBTTagCompound>> write;
        public final Consumer<Class<T>> reset;
        private Class(String key, T ref, NBTTagCompound nbt, Function<T, BiConsumer<String, NBTTagCompound>> write, Consumer<Class<T>> reset)
        {
            this.key = key;
            this.ref = ref;
            this.write = write;
            this.nbt = nbt;
            this.reset = reset;
        }
        public void ResetRef(T ref) { this.ref = ref; }
        public T Get() { return ref; }
        void Write() {if (!"".equals(key)) write.apply(ref).accept(key, nbt);}
        public void Reset() {reset.accept(this);}
    }
}
