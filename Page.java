
import java.util.Arrays;

/*
f. The Page is a data structure which holds the Memory frame
The Memory is divided to definite Number of Pages instead of words
The DISK is similarily divided into Pages than No of words
This class acts as a datastructure for Page
*/
public class Page {
    
    
    int frames[]=new int[Variables.PAGE_SIZE];
    
    public Page(String a, String b, String c, String d){
        frames[0]=Variables.tryParse(a);
        frames[1]=Variables.tryParse(b);
        frames[2]=Variables.tryParse(c);
        frames[3]=Variables.tryParse(d);
        
    }
    /*
    Adds Words to page
    */
    public Page(String[] fr){
        for(int i=0;i<Variables.PAGE_SIZE;i++){
            if(fr[i]==null){
                frames[i]=Variables.tryParse("-1");
            }
            else{
                frames[i]=Variables.tryParse(fr[i]);
            }
        }
    }
    /*
    Initial Array Allocation
    */
    public Page(){
        Arrays.fill(frames, -1);
    }
    /*
    Add single word to page
    */
    protected void addFrame(String data,int i){
        frames[i]=Variables.tryParse(data);
    }
    /*
    Debug Method
    */
    void display() {
        for(int i=0;i<frames.length;i++){
            System.out.println("Frame "+i+":"+Integer.toHexString(frames[i]));
        }
    }
    /*
    Returns Word from a page
    */
    int getFrame(int IPCdisp) {
        return frames[IPCdisp];
    }
    
    /*
    Returns the Page Length which are actually set
    */
    
    int getPageLength() {
        int x=0;
        for(int i=0;i<frames.length;i++){
            if(frames[i]!=-1){
                x++;
            }
        }
        return x;
    }
    
}
