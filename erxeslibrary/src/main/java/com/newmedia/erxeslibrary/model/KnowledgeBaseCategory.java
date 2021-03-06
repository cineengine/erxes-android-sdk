package com.newmedia.erxeslibrary.model;

import android.util.Log;

import com.newmedia.erxes.basic.FaqGetQuery;
import com.newmedia.erxes.basic.MessengerSupportersQuery;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class KnowledgeBaseCategory extends RealmObject {
    @PrimaryKey
    public String _id;
    public String title;
    public String description;
    public int numOfArticles;
    public RealmList<User> authors;
    public RealmList<KnowledgeBaseArticle> articles;
    public String icon;
    static public List<KnowledgeBaseCategory> convert(List<FaqGetQuery.Category> itemuser){
        KnowledgeBaseCategory temp;
        List<KnowledgeBaseCategory> categories = new ArrayList<>();
        for(int  i = 0 ; i <  itemuser.size(); i++ ) {
            temp = new KnowledgeBaseCategory();
            temp._id = itemuser.get(i)._id();
            temp.title = itemuser.get(i).title();
            temp.icon = itemuser.get(i).icon();
            temp.description = itemuser.get(i).description();
            temp.numOfArticles = itemuser.get(i).numOfArticles().intValue();
            temp.articles = new RealmList<>();
            temp.articles.addAll(KnowledgeBaseArticle.convert(itemuser.get(i).articles()));
            categories.add(temp);
        }
        return categories;
    }
}
