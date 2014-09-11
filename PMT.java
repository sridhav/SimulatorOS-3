
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
f. PMT is the Page Map Table.
The Page Map Table holds the data about which page is located
in which memory frame.

The PMT has reference, modify(dirty) and a reference pointer
Which is used in the second chance Algorithm

*/
public class PMT {
    int pageNo;
    int pageArr[];
    int reference[];
    int modify[];
    int index;
    int referencePointer=0;
    List setPageArray=new ArrayList();
    
    public PMT(int x){
        pageArr=new int[x];
        Arrays.fill(pageArr, -1);
        reference=new int[x];
        modify=new int[x];
        index=0;
    }
    
    /*
    Adds an entry into the PMT
    */
    public void setPageToMem(int pageNo,int frameAddress){
        pageArr[pageNo]=frameAddress;
    }
    /*
    Checks whether the page is set or not
    */
    
    public boolean isPageSet(int pageNo) throws IOException{
        //      System.out.println(pageArr[pageNo]);
        boolean m=false;
        try{
            m=(pageArr[pageNo]!=-1);
        }
        catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_MEM_OUT_OF_RANGE);
        }
        return m;
    }
    /*
    Returns the Memory Frame address of the of the page No if set
    */
    
    int getMemoryFrame(int page){
        return pageArr[page];
    }
    /*
    Increments reference pointer uses a circular queue because second chance
    algorithm is used.
    */
    void incrementReferencePointer() throws IOException{
        referencePointer++;
        int arr[]=getSetPageNos();
        referencePointer=referencePointer%(arr.length);
    }
    /*
    DEBUG METHOD
    */
    String display(){
        String temp="";
        for(int i=0;i<pageArr.length;i++){
            temp=temp+""+pageArr[i];
        }
        return temp;
    }
    
    /*
    Returns the array of page Nos which are set.
    */
    int[] getSetPageNos() throws IOException{
        int count=0;
        for(int i=0;i<pageArr.length;i++){
            if(isPageSet(i)){
                count++;
            }
        }
        int[] arr=new int[count];
        int j=0;
        for(int i=0;i<pageArr.length;i++){
            if(isPageSet(i)){
                arr[j]=i;
                j++;
            }
        }
        
        return arr;
    }
     void releaseAllFrames() {
        for(int i=0;i<pageArr.length;i++){
            Page pg=new Page();   
            Variables._MEM[i]=pg;
            Variables.MEM_FMBV[i]=-1;
        }
    }
}
