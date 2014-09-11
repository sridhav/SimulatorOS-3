
import java.io.FileWriter;

/*
f. the Loader is responsible for loading the data from input(file) to Memory. The hex file is loaded
and the Loader calls the Memory Module to write to memory. It Handles two errors Invalid Trace Flag
error and Invalid Loader Format Error.

g. The Loader Job accurately implements the Specification.

*/
public class Loader {
    
    public Loader() throws Exception{
        if(Variables.getFreeMemory()!=-1 && Variables.runningJob<Variables.JOB_PCBS.size()){
            readLoader(Variables.runningJob);
        }
        
    }
      /*
    The readLoader Method takes the data from the file and writes them into the Memory.
    The Initial PC, Trace flag, mem_size, Load Address and JOB_ID gets stored in global variables
    which are given in the file.
    
    */
  
    public void readLoader(int x) throws Exception {
            PCB currentPCB=Variables.JOB_PCBS.get(x);
            PCB pcb=Variables.currentPCB;
            int seg=Variables.currentSegment;
            
            Variables.currentPCB=currentPCB;
            Variables.currentSegment=currentPCB.currentSegment;
            
            int pcb_no=Variables.current_pcb_no;
            
            Variables.current_pcb_no=x;
            Variables.currentPCB.jobNo=Variables.current_pcb_no;
           
            Variables.readyQueue.enqueue(Variables.runningJob);
            System.out.println("LOADER :ADDING "+Variables.runningJob);
            Variables.runningJob++;
            
            
            int ex0=(int) Math.ceil((double)(currentPCB.progSegSize/(double)Variables.PAGE_SIZE));
            allocateFrames(0,ex0);
            Memory mem=new Memory();
            int IPCPage=Variables.getPageNo(currentPCB.IPC);
            int IPCdisp=Variables.getPageOffset(currentPCB.IPC);
            Page newPage=Variables.getPageFromDisk(IPCPage);            
            int Z=newPage.getFrame(IPCdisp);
            mem.writeMemory(currentPCB.IPC,Z);
            if(Variables.currentPCB.CPU_TRACE==1){
                FileWriter fw=new FileWriter("trace_file_"+Variables.current_pcb_no+".txt",false);
                fw.write("PC  \tBR  \tIR  \tTOS \tS[TOS]\tEA  \tMEM[EA]\tTOS \tS[TOS]\tEA  \tMEM[EA]\t\n\n");
                fw.close();
            }
            
            Variables.currentPCB=pcb;
            Variables.currentSegment=seg;
            Variables.current_pcb_no=pcb_no;
    }
    
    /*
            Allocates Initial frames for the Job
    */
    
    public static void allocateFrames(int x,int ex0) {
        PCB pcb=Variables.currentPCB;
        int noOfFrames=pcb.noOfFrames;
        if(noOfFrames>=5){
            noOfFrames=5;
            Variables.maxAllocatedFrames=5;
        }
        Variables.currentPCB.createPMT(x, ex0);
    }
}
