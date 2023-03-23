package jp.mochisystems.core.util;

public class HashMaker {

    private int hash;

    public HashMaker(){
        hash = 1;
    }
    public HashMaker(int init){
        hash = init;
    }

    public HashMaker Append(int i) { hash = hash * 31 + i; return this; }
    public HashMaker Append(char c) { hash = hash * 31 + c; return this; }
    public HashMaker Append(String s)
    {
        for(char c : s.toCharArray()) Append(c);
        return this;
    }

    public int GetHash()
    {
        return hash;
    }
}
