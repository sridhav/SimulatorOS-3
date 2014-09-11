
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
g. So in order to have the global Variables to be distributed among these classes I have used Variables class
and declared some static public variables which can be used by all Classes. The imp Global Variables like
PC,IR, CLOCK, MEM etc., are declared here.
*/

public class Variables {
    
    
    static int PMTPointer=0;
    static ArrayList<PCB> JOB_PCBS=new ArrayList<PCB>();
    public static int pageReplacePointer=0;
    public static String Message="";
    
    public static int currentSegment=0;
    public static PMT currentProgPMT;
    public static PMT currentInpPMT;
    public static PMT currentOutPMT;
    public static int TOS;
    public static int PC;
    public static int IR;
    public static int BR;
    public static int EA;
    
    public static int PAGE_SIZE=8;
    public static Stack _STACK=new Stack();
    public static Page _MEM[]=new Page[256];
    public static int MEM_FMBV[]=new int[256];
    public static Page[] _DISK=new Page[256];
    public static int DISK_FMBV[]=new int[256];
    
    /*
    USED FOR CHECKING AN INFINITE LOOP
    */
    
    public static int prev_index_value=0;
    public static int now_index_value=0;
    public static int index_count=0;
    
    /*
    LOADER OBTAINED VARIABLES FROM INPUT FILE.
    */
    
    public static int JOB_ID;
    public static int LA;
    public static int IPC;
    public static int CPU_TRACE=0;
    public static int mem_size;
    
    
    public static long _CLOCK=0;
    public static long IO_CLOCK=0;
    
    public static int prev_tos=0;
    public static int prev_ea=0;
    public static String prev_stack_val;
    
    /*
    LOADER INPUT FILE
    */
    
    public static String loadFile;
    
    /*
    OUTPUT FROM WRITE. Trace date to be written into Trace file
    */
    
    public static String OUTPUT="";
    public static String INPUT="";
    public static String traceData="";
    static PCB currentPCB=null;
    static int maxAllocatedFrames;
    static int PAGE_FAULT_CLOCK=0;
    static int SEGMENT_FAULT_CLOCK=0;
    
    static Page lastPage=null;
    static int mem_val;
    static int prev_ea_val;
    static int currentCPUTime=0;
    static int current_pcb_no=-1;
    static Queue blockedQueue=new Queue();
    static Queue readyQueue=new Queue();
    static int runningJob=0;
    static int disk_size;
    static String normalCount;
    static String abnormalCount;
    static String abnormalTimeLost;
    static String infTimeLost;
    static Object infiniteJobs;
    static String meanTA;
    static String meanWA;
    static String meanPF;
    static String JOBS_PROCESSED;
    static ArrayList<ArrayList<Integer>> RUN_TIMES;
    static ArrayList<ArrayList<Integer>> TATIMES;
    static ArrayList<ArrayList<Integer>> PROGSIZES;
    static ArrayList<ArrayList<Integer>> LOADSIZES;
    static ArrayList<ArrayList<Integer>> INPUTSIZES;
    static ArrayList<ArrayList<Integer>> LOADINPSIZES;
    static ArrayList<ArrayList<Integer>> OUTSIZES;
    static ArrayList<ArrayList<Integer>> LOADOUTSIZES;
    static ArrayList<ArrayList<Integer>> CPU_SHOTS;
    static ArrayList<ArrayList<Integer>> IO_REQS;
    
    /*
    Method to write to trace_file.txt
    */
    
    public static void writeToTraceFile() throws IOException,NoSuchElementException{
        FileWriter fw=new FileWriter("trace_file_"+Variables.current_pcb_no+".txt",true);
        String temp="";
        String Stack_val;
        if(Variables._STACK.isEmpty()){
            Stack_val="EMPTY";
        }
        else{
            int val=(int) Variables._STACK.pop();
            Stack_val=toHex(val);
            Variables._STACK.push(val);
        }
		  
        temp=temp+"PC  \tBR  \tIR  \tTOS \tS[TOS]\tEA  \tMEM[EA]\tTOS \tS[TOS]\tEA  \tMEM[EA]\t\n\n";
        temp=temp+""+toHex(Variables.PC)+"\t"+toHex(Variables.BR)+"\t"+toHex(Variables.IR)+"\t"+toHex(Variables.prev_tos)+"\t"+prev_stack_val+"\t"+toHex(prev_ea)+"\t"+toHex(prev_ea_val)+"\t"+toHex(Variables._STACK.size())+"\t"+Stack_val+"\t"+toHex(EA)+"\t"+toHex(mem_val);
        temp=temp+"\n";
        fw.write(temp);
        fw.close();
    }
    
    /*
    Write to Memory Dump
    */
    
    private static String writeMemoryDump() {
        String temp2="MEMORY DUMP\n";
        temp2=temp2+"HEX \t"+ "Binary Value\n";
        for(int i=0;i<mem_size;i++){
            int x=0;//_MEM[i];
            String temp="000000"+Integer.toHexString(x);
            String temp3="00000000000000000000000000000"+Integer.toBinaryString(x);
            temp3=temp3.substring(temp3.length()-16,temp3.length());
            temp2=temp2+temp.substring(temp.length()-4,temp.length())+"\t"+temp3+"\n";
        }
        return "";
    }
    
    /*
    converts to Hex values;
    */
    
    public static String toHex(int val) {
        String temp="000000000"+Integer.toHexString(val);
        temp=temp.substring(temp.length()-4,temp.length());
        return temp;
    }
    
    /*
    Debug Methods while coding & Testing
    Displays all global Variables,Mem & Stack
    */
    
    public static void display(){
        System.out.format("Top of Stack :%-30d",TOS);
        System.out.format("Program Counter:%-30d",PC);
        System.out.format("Instruction Register:%-30d",IR);
        System.out.format("Base Register:%-30d",BR);
        System.out.format("Effective Address:%-30d",EA);
        System.out.format("Job ID:%-30d", JOB_ID);
        System.out.format("Load Address:%-30d", LA);
        System.out.format("Initial Program Counter:%-30d", IPC);
        System.out.format("Memory Size:%-30d",mem_size);
        System.out.format("Clock:%-30d",_CLOCK);
        displayMem();
        displayStack();
    }
    
    public static void writeOutputFile(){
		Spooler m=new Spooler();
		m.updateExecutionProfile();
	}
	
    /*
    Debug Methods while coding & Testing
    Displays Memory
    */
    public static void displayMem() {
        System.out.println("\n\n#######MEMORY#########");
        for(int i=0;i<255;i++){
            String hex=Integer.toHexString(0);//_MEM[i]);
            String bin=Integer.toBinaryString(0);//_MEM[i]);
            hex="000000000"+hex;
            hex=hex.substring(hex.length()-4, hex.length());
            bin="0000000000000000000000000000000000000000"+bin;
            bin=bin.substring(bin.length()-16, bin.length());
            hex=String.format("%4s",hex).replace(' ', '0');
            bin=String.format("%32s",bin).replace(' ', '0');
            System.out.println(_MEM[i]+"\t"+hex+"\t"+bin);
            
        }
    }
    
    /*
    Debug Methods while coding & Testing
    Displays Stack Vals
    */
    
    public static void displayStack() {
        System.out.println("#########STACK###########");
        Iterator m=_STACK.iterator();
        while(m.hasNext()){
            int val=(int) m.next();
            String hex=Integer.toHexString(val);
            String bin=Integer.toBinaryString(val);
            hex="000000000"+hex;
            hex=hex.substring(hex.length()-4, hex.length());
            bin="0000000000000000000000000000000000000000"+bin;
            bin=bin.substring(bin.length()-16, bin.length());
            hex=String.format("%4s", hex).replace(' ', '0');
            bin=String.format("%32s",bin).replace(' ','0');
            System.out.println(val+"\t"+hex+"\t"+bin);
        }
    }
    /*
    Randomizes DISK FMBV
    */
    public static void randomizeDiskFMBV(){
        for(int i=0;i<100;i++){
            int temp=(int)Math.round(Math.random()*100);
            DISK_FMBV[temp]=1;
        }
    }
    
    /*
    reutns the pageNo for a MEMORY Address
    */
    public static int getPageNo(int mem_val){
        return mem_val/PAGE_SIZE;
    }
    /*
    returns the Offset for a MEMORY Address
    */
    
    public static int getPageOffset(int mem_val){
        return mem_val%PAGE_SIZE;
    }
    /*
    return Page From DISK
    */
    public static Page getPageFromDisk(int pageNo) throws IOException{
        Page pg=null;
        try{
            if(Variables.currentSegment==0){
                int diskPageAddr=(int) Variables.currentPCB.progPageFrames.get(pageNo);
                pg=Variables._DISK[diskPageAddr];
            }
            else if(Variables.currentSegment==1){
                int diskPageAddr=(int) Variables.currentPCB.inpPageFrames.get(pageNo);
                pg=Variables._DISK[diskPageAddr];
            }
            else if(Variables.currentSegment==2){
                int diskPageAddr=(int) Variables.currentPCB.outPageFrames.get(pageNo);
                pg=Variables._DISK[diskPageAddr];
            }
        }catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_PAGE_NOT_FOUND);
        }
        if(pg==null){
            //HANDLE ERROR NO PAGE FOUND IN MEMORY
            ErrorHandler.throwError(ErrorHandler.ER_PAGE_NOT_FOUND);
        }
        return pg;
    }
    /*
    returns Page from Memory
    */
    public static Page getPageFromMem(int pageNo) throws IOException{
        PMT pmt=getCurrentPMT();
        Page page=null;
        page=_MEM[pmt.getMemoryFrame(pageNo)];
        if(page==null){
            //HANDLE ERROR NO PAGE FOUND IN MEMORY
            ErrorHandler.throwError(ErrorHandler.ER_PAGE_NOT_FOUND);
        }
        return page;
    }
    /*
    returns current PMT
    */
    public static PMT getCurrentPMT() throws IOException{
        PMT pmt=null;
        if(Variables.currentSegment==1){
            pmt=Variables.currentPCB.progSegment;
        }
        else if(Variables.currentSegment==1){
            pmt=Variables.currentPCB.inpSegment;
        }
        else if(Variables.currentSegment==2){
            pmt=Variables.currentPCB.outSegment;
        }
        if(pmt==null){
            //HANDLE ERROR INVALID PMT\
            ErrorHandler.throwError(ErrorHandler.ER_NO_PMT_FOUND);
        }
        return pmt;
    }
    
    /*
    TRY PARSING INTEGER TO HEX
    */
    public static Integer tryParse(String text){
        try{
            return Integer.parseInt(text,16);
        }catch(Exception e){
            //PLACE ERROR HANDLER INPUT LOADER FORMAT INVALID NUMBER FORMAT
            //  ErrorHandler.throwError(ErrorHandler.ER_LOADER_INVALID_FORMAT);
            return 0;
        }
    }
    
    /*
    Page Map Table updation
    */
    static void updatePMT(PMT pmt) {
        if(Variables.currentSegment==0){
            Variables.currentPCB.progSegment=pmt;
        }
        else if(Variables.currentSegment==1){
            Variables.currentPCB.inpSegment=pmt;
        }
        else if(Variables.currentSegment==2){
            Variables.currentPCB.outSegment=pmt;
        }
        
    }
    static void removePage(int i) {
        DISK_FMBV[i]=0;
        Page pg=new Page();
        _DISK[i]=pg;
    }
    static int getFreeMemory(){
        int i;
        for(i=0;i<MEM_FMBV.length;i++){
            if(MEM_FMBV[i]==0){
                i++;
            }
        }
        if(i<6)
            return -1;
        
        return i;
        
    }
}
