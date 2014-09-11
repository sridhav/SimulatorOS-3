
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
f.Page faults are handled here

Intially Frames are allocated. If the Max Allocated Frames is greater than 6
The allocated frames are replaced using the replacement algorithm

For Replacmeent, Second Chance Algorithm has been used.

If the dirty(modify) bit it set then the page is copied to the disk before replacement.



*/

class PageFaultHandler {
    
    int memoryAddress;
    PMT pmt=null;
    public PageFaultHandler(int address) throws IOException{
        memoryAddress=address;
        handlePageFault();
    }
    /*
    Handles Page Faults
    */
    private void handlePageFault() throws IOException {
        if(Variables.currentPCB.allocatedFrames>Variables.maxAllocatedFrames){
            runReplacement();
        }
        else{
            int val=checkForFreeFMBV();
            if(val<0){
                runReplacement();
            }
            else{
                int pageNo=Variables.getPageNo(memoryAddress);
                Page page=Variables.getPageFromDisk(pageNo);
                Variables.lastPage=page;
                Variables._MEM[val]=page;
                adjustPMT(pageNo,val);
                
                pmt=Variables.getCurrentPMT();
                pmt.setPageArray.add(pageNo);
                Variables.updatePMT(pmt);
                Variables.currentPCB.allocatedFrames++;
            }
        }
        
    }
    
    /*
    Checks for Free Memory Frames in the MEMORY FMBV VECTOR
    */
    private int checkForFreeFMBV() {
        for(int i=0;i<Variables.MEM_FMBV.length;i++){
            if(Variables.MEM_FMBV[i]==0){
                Variables.MEM_FMBV[i]=1;
                return i;
            }
        }
        return -1;
    }
    /*
    Adjusts the PMT Whenever replacement takes place
    */
    
    private void adjustPMT(int pageNo,int val) throws IOException {
        PMT pmt=Variables.getCurrentPMT();
        pmt.setPageToMem(pageNo, val);
        pmt.reference[pmt.referencePointer]=1;
        Variables.updatePMT(pmt);
        
    }
    /*
    Page is Copied to disk whenever the dirty bit is set
    */
    
    private void copyPageToDisk(int pageNo,int replaceFrame) {
        Page memPage=Variables._MEM[pageNo];
        Variables._DISK[replaceFrame]=memPage;
    }
    
    /*
    Second Chance Algorithm is used for Page Replacement
    */
    
    private void runReplacement() throws IOException {
        pmt=Variables.currentPCB.progSegment;
        
        while(pmt.reference[(int)pmt.setPageArray.get(pmt.referencePointer)]!=0){
            pmt.reference[(int)pmt.setPageArray.get(pmt.referencePointer)]=0;
            pmt.incrementReferencePointer();
        }
        
        Variables.updatePMT(pmt);
        pmt=Variables.getCurrentPMT();
        
        
        
        int pageToBeReplaced=(int)pmt.setPageArray.get(pmt.referencePointer);
        int replaceMemFrame=pmt.getMemoryFrame(pageToBeReplaced);
        
        if(pmt.modify[pageToBeReplaced]!=0){
            int diskFrame=getDiskFrame(pageToBeReplaced);
            System.out.println(diskFrame);
            copyPageToDisk(replaceMemFrame, diskFrame);
        }
        
        int pageNo=Variables.getPageNo(memoryAddress);
        Page page=Variables.getPageFromDisk(pageNo);
        Variables.lastPage=page;
        pmt=Variables.getCurrentPMT();
        pmt.setPageArray.remove(pmt.referencePointer);
        pmt.setPageArray.add(pmt.referencePointer,pageNo);
        Variables.updatePMT(pmt);
        
        adjustPMT(pageToBeReplaced,-1);
        Variables._MEM[replaceMemFrame]=page;
        adjustPMT(pageNo,replaceMemFrame);
        pmt=Variables.getCurrentPMT();
        pmt.reference[pageNo]=1;
        Variables.updatePMT(pmt);
    }
    
    /*
    Gets the Page from disk to replace it with page from Memory
    */
    
    static int getDiskFrame(int pageToBeReplaced) {
        PCB pcb=Variables.currentPCB;
        ArrayList al=null;
        if(Variables.currentSegment==0){
            System.out.println("AM HERE BABY1");
            al=(ArrayList) pcb.progPageFrames;
        }
        else if(Variables.currentSegment==1){
            System.out.println("AM HERE BABY2");
            al=(ArrayList) pcb.inpPageFrames;
        }
        else if(Variables.currentSegment==2){
            System.out.println("AM HERE BABY3");
            al=(ArrayList)pcb.outPageFrames;
        }
        int retval=-1;
        for(int i=0;i<al.size();i++){
            if(i==pageToBeReplaced){
                retval=(int)al.get(i);
                break;
            }
        }
        return retval;
    }
}
