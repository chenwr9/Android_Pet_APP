package com.javaone.onepet;

public class Pet {
    public int id;
    public String belong;
    public String name;
    public int sex;
    public int level;
    public String character;
    public String partner;
    public String wechat;
    public int model;

    Pet() {
        belong = "self";
        name = "未设置";
        sex = 1;
        level = 1;
        character = "乖巧";
        partner = "未结对";
        wechat = "未绑定";
        model = 1;
    }
}
