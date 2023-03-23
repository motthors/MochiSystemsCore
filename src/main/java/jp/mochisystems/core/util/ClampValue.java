package jp.mochisystems.core.util;


public class ClampValue {
    public static class Int {
        private Int(){}

        private int value, min, max;
        public static ClampValue.Int of(int def, int min, int max)
        {
            ClampValue.Int v = new ClampValue.Int();
            v.value = def;
            v.min = min;
            v.max = max;
            return v;
        }
        public void Set(int value)
        {
            if(value < min) value = min;
            if(value > max) value = max;
            this.value = value;
        }
        public int Get()
        {
            return value;
        }
    }

    public static class Float {
        private Float(){}

        private float value, min, max;
        public static ClampValue.Float of(float def, float min, float max)
        {
            ClampValue.Float v = new ClampValue.Float();
            v.value = def;
            v.min = min;
            v.max = max;
            return v;
        }
        public void Set(float value)
        {
            if(value < min) value = min;
            if(value > max) value = max;
            this.value = value;
        }
        public float Get()
        {
            return value;
        }
    }
}
