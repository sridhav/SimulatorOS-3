
import java.util.ArrayList;
import java.util.List;

/*
f. The PCB holds the Info about segmentss.
The pointers to the Segments
And the location of Page frames in the disk

*/
public class PCB {
    static int RUNNING=1;
    static int BLOCKED=2;
    static int READY=3;
    static int COMPLETED=4;
    int status;
    int JOB_ID;
    int jobNo;
    PMT progSegment;
    PMT inpSegment;
    PMT outSegment;
    List progPageFrames;
    List inpPageFrames;
    List outPageFrames;
    int progSegSize;
    int LA;
    int IPC;
    int CPU_TRACE;
    int inpSegSize;
    int outSegSize;
    int allocatedFrames=0;
    int inpIndex=0;
    int outIndex=0;
    int noOfFrames=0;
    int execTime=0;
    Stack pcbStack=new Stack();
    int expectedIOTime;
    int currentSegment;
    
    
    public int prev_tos=0;
    public int prev_ea=0;
    public String prev_stack_val;
    
    public int prev_index_value=0;
    public int now_index_value=0;
    public int index_count=0;
    
    public  String OUTPUT="";
    public  String INPUT="SRID";
    public  String traceData="";
    
    public int EA;
    
    public List inputList=new ArrayList();
    public List outputList=new ArrayList();
    public PMT currentPMT;
    int actualCodeSize;
    
    int CPUshots=0;
    int IOshots=0;
    
    int turnAroundTime=0;
    int outputs=0;
    int inputs=0;
    
    int entryTime=0;
    int pageFaults=0;
    int IOTime=0;
    int PAGE_FAULT_TIME=0;
    int SEGMENT_FAULT_TIME=0;
    
    
    public PCB() {
        this.progPageFrames = new ArrayList();
        this.inpPageFrames = new ArrayList();
        this.outPageFrames = new ArrayList();
    }
    /*
    Creates a PMT for each Segment
    */
    
    protected void createPMT(int segNo,int size){
        if(segNo==0){
            progSegment=new PMT(size);
        }
        else if(segNo==1){
            inpSegment=new PMT(size);
        }
        else if(segNo==2){
            outSegment=new PMT(size);
        }
       
    }
    /*
    Debug Method
    */
    protected void display(){
        System.out.println("JOB ID:"+jobNo);
        System.out.println("Prog Segment:"+progSegment);
        System.out.println("Input Segment"+inpSegment);
        System.out.println("Input Segment"+outSegment);
        
        System.out.println("####Prog Frames#####");
        for(Object i:progPageFrames){
            System.out.println((int)i);
        }
        System.out.println("#####inp Frames#####");
        for(Object i:inpPageFrames){
            System.out.println((int)i);
        }
        System.out.println("####out Frames#####");
        for(Object i:outPageFrames){
            System.out.println((int)i);
        }
        System.out.println("####SIZE#####");
        System.out.println("progSegSize"+progSegSize);
        System.out.println("inpSegSize"+inpSegSize);
        System.out.println("outSegSize"+outSegSize);
        System.out.println("####OTHERS####");
        System.out.println("LA"+LA);
        System.out.println("IPC"+IPC);
        System.out.println("CPU TRACE"+CPU_TRACE);
        System.out.println();
    }
}
