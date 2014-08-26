package com.radaee.reader;

import java.util.ArrayList;  
import java.util.List;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.Adapter;  
import android.widget.BaseAdapter;  
public abstract class CategoryAdapter extends BaseAdapter {  
    private List<Category> categories = new ArrayList<Category>();
    private String title;
    private int num_word;
    private int num_pdf;
    
    public void setData(int num_word,int num_pdf) {  
    	this.num_word  = num_word;
    	this.num_pdf = num_pdf;
    }  
      
    public int getNum_word() {  
        return num_word;  
    }
    
    public int getNum_pdf() {  
        return num_pdf;  
    }  

      
    public void addCategory(String title, Adapter adapter) {  
        categories.add(new Category(title, adapter));  
        this.title =  title;
    }  
      
   
    public int getCount() {  
        int total = 0;  
          
        for (Category category : categories) {  
            total += category.getAdapter().getCount() + 1;  
        }  
          
        return total;  
    }  
    
    public Object getItem(int position) {  
        for (Category category : categories) {  
            if (position == 0) {  
                return category;  
            }  
              
            int size = category.getAdapter().getCount() + 1;  
            if (position < size) {  
                return category.getAdapter().getItem(position-1);  
            }  
            position -= size;  
        }  
          
        return null;  
    }  
 
    public long getItemId(int position) {  
        return position;  
    }  
      
    public int getViewTypeCount() {  
        int total = 1;  
          
        for (Category category : categories) {  
            total += category.getAdapter().getViewTypeCount();  
        }  
          
        return total;  
    }  
    public int getItemViewType(int position) {  
        int typeOffset = 1;  
          
        for (Category category : categories) {  
            if (position == 0) {  
                return 0;  
            }  
              
            int size = category.getAdapter().getCount() + 1;  
            if (position < size) {  
                return typeOffset + category.getAdapter().getItemViewType(position - 1);  
            }  
            position -= size;  
              
            typeOffset += category.getAdapter().getViewTypeCount();  
        }  
          
        return -1;  
    }  

    
    public View getView(int position, View convertView, ViewGroup parent) {  
        int categoryIndex = 0;  
          
        for (Category category : categories) {  
            if (position == 0) {  
                return getTitleView(category.getTitle(), categoryIndex,convertView, parent);  
            }  
            int size = category.getAdapter().getCount()+1;  
            if (position < size) {  
                return category.getAdapter().getView(position - 1, convertView, parent);  
            }  
            position -= size;  
              
            categoryIndex++;  
        }  
          
        return null;  
    }  
      
    protected abstract View getTitleView(String caption,int index,View convertView,ViewGroup parent);  
    
    @Override
    public boolean isEnabled(int position) {
        if(position == 0 || position == (num_word +1)||position == (num_word +num_pdf+2) ){
            return false;
        }
        return super.isEnabled(position);
    }

      
}   

