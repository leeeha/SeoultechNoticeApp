package com.mobile.webcrawling;

public class ItemObject implements Comparable<ItemObject> {
    String index, title, link, author, date;
    Integer count;

    ItemObject(String index, String title, String link,
               String author, String date, Integer count){
        this.index = index;
        this.title = title;
        this.link = link;
        this.author = author;
        this.date = date;
        this.count = count;
    }

    @Override // 내림차순 정렬
    public int compareTo(ItemObject obj) {
        if(this.count > obj.count){
            return -1;
        }else if(this.count < obj.count){
            return 1;
        }
        return 0;
    }
}
