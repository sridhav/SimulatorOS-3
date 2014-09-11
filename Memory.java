

import java.io.IOException;

/*

f. The Memory Routine reads and writes data to the Memory. Its the sole job of Memory
An Error Handler for MemoryAddressBound is checked. If error is present it terminates.
MemoryAddressBound -> If EA is greater than 256 | EA is less than 0

g. As Java doesnot allow pass by reference. I could not use the Procedure MEMORY(X,Y,Z).
So I need to create to other Methods readMemory to Read From Memory, and writeMemory
to write to memory. The overall functionality doesnot change except for the Specification

*/

public class Memory {
    
    PMT currentPMT;
    public int readMemory(int Y) throws IOException{
        int pageNo=getPageNo(Y);
        if(checkPMT(pageNo)){
            pageNo=getPageNo(Y);
            currentPMT=Variables.getCurrentPMT();
            System.out.println(currentPMT.display());
            int addr=currentPMT.getMemoryFrame(pageNo);
            int offset=getPageOffset(Y);
            int val=Variables._MEM[addr].getFrame(offset);
            Variables.getCurrentPMT().reference[pageNo]=1;

            return val;
        }
        else{
            PageFaultHandler pfh=new PageFaultHandler(Y);
            Variables.currentPCB.pageFaults++;
            pageNo=getPageNo(Y);
            currentPMT=Variables.getCurrentPMT();
            int addr=currentPMT.getMemoryFrame(pageNo);
            int offset=getPageOffset(Y);
            int val=Variables._MEM[addr].getFrame(offset);
            Variables.getCurrentPMT().reference[pageNo]=1;
            //Variables.PAGE_FAULT_CLOCK=Variables.PAGE_FAULT_CLOCK+10;
            
            return val; 
        }        
    }
    
    public void writeMemory(int Y,int Z) throws IOException{
        int mem_val=Y;
        int pageNo=getPageNo(mem_val);
        if(checkPMT(pageNo)){
            currentPMT=Variables.getCurrentPMT();
            int addr=currentPMT.getMemoryFrame(pageNo);
            int offset=getPageOffset(mem_val);
            Variables._MEM[addr].frames[offset]=Z;
            Variables.getCurrentPMT().reference[pageNo]=1;
            Variables.getCurrentPMT().modify[pageNo]=1;
            
        }
        else{
            PageFaultHandler pfh=new PageFaultHandler(mem_val);
            Variables.currentPCB.pageFaults++;
            currentPMT=Variables.getCurrentPMT();
            int addr=currentPMT.getMemoryFrame(pageNo);
            int offset=getPageOffset(mem_val);
            Variables._MEM[addr].frames[offset]=Z;
        
            Variables.getCurrentPMT().reference[pageNo]=1;
            Variables.getCurrentPMT().modify[pageNo]=1;
        }        
    }
    
    private int getPageOffset(int mem_val) {
        return mem_val%Variables.PAGE_SIZE;
    }

    private int getPageNo(int mem_val) {
        return mem_val/Variables.PAGE_SIZE;
    }
    
    private boolean checkPMT(int pageNo) throws IOException {
        if(Variables.currentSegment==0){
            if(Variables.currentPCB.progSegment==null){
                int ex0=(int) Math.ceil((double)(Variables.currentPCB.progSegSize/(double)Variables.PAGE_SIZE));
                Loader.allocateFrames(0, ex0);
                Variables.SEGMENT_FAULT_CLOCK=Variables.SEGMENT_FAULT_CLOCK+5;
            }
            currentPMT=Variables.currentPCB.progSegment;
        }
        else if(Variables.currentSegment==1){
            if(Variables.currentPCB.inpSegment==null){
                int ex0=(int) Math.ceil((double)(Variables.currentPCB.inpSegSize/(double)Variables.PAGE_SIZE));
                Loader.allocateFrames(1,ex0);
                Variables.SEGMENT_FAULT_CLOCK=Variables.SEGMENT_FAULT_CLOCK+5;
            }
            currentPMT=Variables.currentPCB.inpSegment;
        }
        else if(Variables.currentSegment==2){
            if(Variables.currentPCB.outSegment==null){
                int ex0=(int) Math.ceil((double)(Variables.currentPCB.outSegSize/(double)Variables.PAGE_SIZE));
                Loader.allocateFrames(2,ex0);
                Variables.SEGMENT_FAULT_CLOCK=Variables.SEGMENT_FAULT_CLOCK+5;
            }
            currentPMT=Variables.currentPCB.outSegment;
            
        }
        else{
            ErrorHandler.throwError(ErrorHandler.ER_MEM_OUT_OF_RANGE);
        }
        return currentPMT.isPageSet(pageNo);        
    }
}
