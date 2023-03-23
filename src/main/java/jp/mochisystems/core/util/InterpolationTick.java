package jp.mochisystems.core.util;

import jp.mochisystems.core._mc._core.Logger;

public class InterpolationTick {

	float now;
	float prev;
	
	public InterpolationTick()
	{
		Init(0.0f);
	}
	public InterpolationTick(float init)
	{
		Init(init);
	}
	public void Init(float init)
	{
		now = init;
		prev = init;
	}
	
	public void update()
	{
		prev = now;
	}
	
	public void set(float set)
	{
		now = set;
	}
	
	public void setPrev(float prev)
	{
		this.prev = prev;
	}
	
	public void add(float add)
	{
		now += add;
	}
	
	public void addAll(float add)
	{
		now += add;
		prev += add;
	}


	
	public void clamp(float min, float max)
	{
		if(now > max)now = max;
		else if(now < min)now = min;
	}
	
	public float get()
	{
		return now;
	}
	
	public float getPrev()
	{
		return prev;
	}
	
	public float getFix(float partialtick)
	{
		return prev + (now - prev)*partialtick;
	}



	public static class Rounded extends InterpolationTick {
		public Rounded(float init)
		{
			Init(init);
		}

//		public void set(float set)
//		{
//			super.set(set);
//		}

		public void add(float add)
		{
			super.add(add);
			round();
		}

		public void addAll(float add)
		{
			super.addAll(add);
			round();
		}

		public void roundNow()
		{
			if(now>180f)add(-360f);
			else if(now<-180f)add(360f);
		}

		public void round()
		{
			if(now>180f)addAll(-360f);
			else if(now<-180f)addAll(360f);
		}
	}
}
