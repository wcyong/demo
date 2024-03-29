package org.wcy.activimq;

import java.io.Serializable;

public class Email implements Serializable {
  
    /**  
     *   
     */  
    private static final long serialVersionUID = 3374150870949603919L;  
  
    private String receiver;  
    private String title;  
    private String content;  
  
    public Email() {  
    }  
  
    public Email(String receiver, String title, String content) {  
        super();  
        this.receiver = receiver;  
        this.title = title;  
        this.content = content;  
    }  
  
    public String getReceiver() {  
        return receiver;  
    }  
  
    public void setReceiver(String receiver) {  
        this.receiver = receiver;  
    }  
  
    public String getTitle() {  
        return title;  
    }  
  
    public void setTitle(String title) {  
        this.title = title;  
    }  
  
    public String getContent() {  
        return content;  
    }  
  
    public void setContent(String content) {  
        this.content = content;  
    }  
  
    @Override  
    public String toString() {  
        return "Email [receiver=" + receiver + ", title=" + title + ", content=" + content + "]";  
    }  
  
}  