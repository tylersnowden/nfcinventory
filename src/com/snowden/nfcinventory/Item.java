package com.snowden.nfcinventory;

public class Item {
    
    //private variables
    int _id;
    String _tag;
    String _name;
    int _status;
     
    // Empty constructor
    public Item(){
         
    }
    // constructor
    public Item(int id, String tag, String name, int status){
        this._id = id;
        this._tag = tag;
        this._name = name;
        this._status = status;
    }
     
    // constructor
    public Item(String name, String tag, int status){
        this._name = name;
        this._tag = tag;
        this._status = status;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
    
    // getting tag
    public String getTag(){
        return this._tag;
    }
     
    // setting name
    public void setTag(String tag){
        this._tag = tag;
    }
     
    // getting name
    public String getName(){
        return this._name;
    }
     
    // setting name
    public void setName(String name){
        this._name = name;
    }
     
    // getting status
    public int getStatus(){
        return this._status;
    }
     
    // setting status
    public void setStatus(int status){
        this._status = status;
    }
}
