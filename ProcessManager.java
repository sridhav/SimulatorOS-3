
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
f. The process manager is event driven. Dependant on the message from CPU
the CPU acts accordingly.
*/
public class ProcessManager {
    /*
    Starts the Job runs until Ready Queue and Blocked queue are empty
    */
    public static void startJob(){
        while(true){
            if(Variables.Message.equals("HALT")){
                releaseResources(Variables.current_pcb_no);
                System.out.println("Back Here");
                Variables.Message="";
                System.out.println("Ready Queue"+Variables.readyQueue.size());
                System.out.println("Ready Queue"+Variables.blockedQueue.size());
            }
            
            System.out.println("PRocess Strated");
            updateBlockedQueue();
            addNewJobs();
            int dispatchJob=(int)Variables.readyQueue.dequeue();
            System.out.println("Dispatch JOB "+dispatchJob);
            Variables.current_pcb_no=dispatchJob;
            PCB pcb=Variables.JOB_PCBS.get(dispatchJob);
            dispatch(pcb);
            try {
                CPU cpu=new CPU(Variables.PC,Variables.CPU_TRACE);
            } catch (Exception ex) {
                Logger.getLogger(ProcessManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            if(Variables.Message.equals("TIME_EXPIRED")){
                Variables.readyQueue.enqueue(Variables.current_pcb_no);
                Variables.Message="";
                System.out.println("TIME_EXPIRED");
                contextSwitch();
                SYSTEM.updateDisplay();
            }
            if(Variables.Message.equals("IO_SWITCH")){
                Variables.blockedQueue.enqueue(Variables.current_pcb_no);
                System.out.println("AME HERER");
                contextSwitch();
                SYSTEM.updateDisplay();
                Variables.Message="";
            }
            if(Variables.readyQueue.isEmpty() && !Variables.blockedQueue.isEmpty()){
                Variables.currentCPUTime=20;
                Variables.Message="";
            }
            if(Variables.blockedQueue.isEmpty() && Variables.readyQueue.isEmpty()){
                SYSTEM.updateDisplay();
                break;
            }
            
            
            
        }
    }
    /*
    Updates the Blocked Queue
    */
    public static void updateBlockedQueue(){
        Iterator itr=Variables.blockedQueue.iterator();
        while(itr.hasNext()){
            int item=(int)itr.next();
            PCB pcb=Variables.JOB_PCBS.get(item);
            pcb.expectedIOTime=pcb.expectedIOTime-Variables.currentCPUTime;
            if(pcb.expectedIOTime<=0){
                Variables.readyQueue.enqueue(item);
                System.out.println("Item Enqueed " + item);
                Variables.blockedQueue.dequeue();
            }
            Variables.JOB_PCBS.set(item, pcb);
        }
    }
    /*
    Calls the Loader to add new Jobs
    */
    public static void addNewJobs(){
        try {
            Loader l=new Loader();
        } catch (Exception ex) {
        }
    }
    
    /*
    Context Switching takes Place
    */
    private static void contextSwitch() {
        System.out.println("In Context Switch");
        if(Variables.currentPCB!=null){
            PCB pcb=Variables.currentPCB;
            pcb.IPC=Variables.PC;
            pcb.CPU_TRACE=Variables.CPU_TRACE;
            pcb.JOB_ID=Variables.JOB_ID;
            pcb.LA=Variables.LA;
            pcb.pcbStack=Variables._STACK;
            pcb.execTime=pcb.execTime+Variables.currentCPUTime;
            pcb.currentSegment=0;
            pcb.prev_tos=Variables.prev_tos;
            pcb.prev_ea=Variables.prev_ea;
            pcb.prev_stack_val=Variables.prev_stack_val;
            pcb.prev_index_value=Variables.prev_index_value;
            pcb.now_index_value=Variables.now_index_value;
            pcb.index_count=Variables.index_count;
            pcb.traceData=Variables.traceData;
            pcb.EA=Variables.EA;
            Variables.JOB_PCBS.set(Variables.current_pcb_no,pcb);
        }
    }
    /*
    Dipatching is done using this function.
    */
    private static void dispatch(PCB pcb) {
        if(pcb.execTime==0){
            pcb.entryTime=(int)Variables._CLOCK;
        }
        Variables.PC=pcb.IPC;
        Variables.EA=pcb.EA;
        Variables.CPU_TRACE=pcb.CPU_TRACE;
        Variables.JOB_ID=pcb.JOB_ID;
        Variables.LA=pcb.LA;
        Variables._STACK=pcb.pcbStack;
        Variables.currentSegment=0;
        Variables.prev_tos=pcb.prev_tos;
        Variables.prev_ea=pcb.prev_ea;
        Variables.prev_stack_val=pcb.prev_stack_val;
        Variables.prev_index_value=pcb.prev_index_value;
        Variables.now_index_value=pcb.now_index_value;
        Variables.index_count=pcb.index_count;
        Variables.traceData=pcb.traceData;
        Variables.currentPCB=pcb;
        Variables.currentCPUTime=0;
        Variables.currentPCB.CPUshots++;
    }
    
    /*
    Resources are released
    */
    
    static void releaseResources(int pcbno) {
        /*
        Release DISK
        */
        PCB pcb=Variables.JOB_PCBS.get(pcbno);
        System.out.println("Releasing Resources");
        if(pcb!=null){
            List k=pcb.progPageFrames;
            for(int i=0;i<k.size();i++){
                Variables.removePage((int)pcb.progPageFrames.get(i));
            }
            k=pcb.inpPageFrames;
            for(int i=0;i<k.size();i++){
                Variables.removePage((int)pcb.inpPageFrames.get(i));
            }
            k=pcb.outPageFrames;
            for(int i=0;i<k.size();i++){
                Variables.removePage((int)pcb.outPageFrames.get(i));
            }
            /*
            Release MEMORY
            */
            PMT pmt=pcb.progSegment;
            pmt.releaseAllFrames();
            /*
            release PCB
            */
            System.out.println("PCB NO DELETED "+Variables.current_pcb_no);
        }
    }
    
    public static void start() throws Exception{
        Runtime rt = Runtime.getRuntime();
        Process p=rt.exec("java -jar ./.dist/.PC.jar "+Variables.loadFile+" execution_profile.txt");
        int exitVal=p.waitFor();
    }
}
