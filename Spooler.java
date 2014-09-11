
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/*
f. The Spooler does two things
1. Input Spool
2. Output Spool

The input spooler takes the file name and extracts data from the
file as kind of compilation.

*/
public class Spooler {
    
    /*
    Input Spooling Starts here
    */
    public  void inputSpooler(String txt) throws IOException{
        readFromFile(txt);
    }
    /*
    Read Data from File
    */
    private void readFromFile(String txt) throws IOException {
        String temp;
        String store="";
        BufferedReader br=null;
        try {
            br = new BufferedReader(new FileReader(txt));
        } catch (FileNotFoundException ex) {
            ErrorHandler.throwError(ErrorHandler.ER_FILE_NOT_FOUND);
        }
        int count=0;
        while((temp=br.readLine())!=null){
            
            if(!temp.equals("\n")){
                store=store+temp+"\n";
            }
            
        }
        
        String[] jobLines=store.split("[**]+");
        System.out.println("JOB LEN SIZE"+jobLines.length);
        int i=1;
        while(i<jobLines.length){
            while(!jobLines[i].contains("JOB")){
                i++;
            }
            if(jobLines[i].contains("JOB")){
                i++;
                if(jobLines[i].contains("INPUT")){
                    i++;
                    if(jobLines[i].contains("FIN")){
                        readJob(jobLines[i-2],jobLines[i-1],jobLines[i]);
                        i++;
                    }
                    else{
                        i++;
                        System.out.println("MISSING FIN");
                        continue;
                        //           ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_OUTPUT);
                    }
                }
                else{
                    i++;
                    System.out.println("MISSING INPUT");
                    continue;
                    //     ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_INPUT);
                }
            }
            else{
                i++;
                System.out.println("MISSING JOB");
                continue;
                // ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_JOB);
            }
            
            try{
            if(!jobLines[i].contains("JOB")){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_JOB);
            }
            i++;
            }catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_JOB);
            }
            try{
            if(!jobLines[i].contains("INPUT")){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_INPUT);
            continue;
            }
            i++;
            }catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_INPUT);
            }
            try{
            if(!jobLines[i].contains("FIN")){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_OUTPUT);
            continue;
            }
            i++;
            }catch(Exception e){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_OUTPUT);
            }
        }
        
    }
    
    /*
    Reads Multiple JOB and gets a new PCB for each job.
    */
    
    private void readJob(String store,String input, String output) throws IOException {
        String jobLineSplit[];
        String lines[];
        PCB pcb=null;
        if(!"".equals(store)){
            lines=store.split("\\r?\\n");
            
            //FIRST LINE OF LOADER
            jobLineSplit=lines[0].split("\\s+");
            pcb=new PCB();
            if(jobLineSplit.length!=3){
                ErrorHandler.throwError(ErrorHandler.ER_LOADER_INVALID_FORMAT);
                
            }
            else{
                if(jobLineSplit[0].toUpperCase().equals("JOB")){
                    pcb.inpSegSize=Variables.tryParse(jobLineSplit[1]);
                    pcb.outSegSize=Variables.tryParse(jobLineSplit[2]);
                }
                else{
                    ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_JOB);
                    
                }
            }
            
            //SECOND LINE OF LOADER
            jobLineSplit=lines[1].split("\\s+");
            
            if(jobLineSplit.length!=5){
                //PLACE ERROR HANDLER INPUT LOADER FORMAT
                ErrorHandler.throwError(ErrorHandler.ER_LOADER_INVALID_FORMAT);
            }
            else{
                pcb.JOB_ID=Variables.tryParse(jobLineSplit[0]);
                pcb.LA=Variables.tryParse(jobLineSplit[1]);
                pcb.IPC=Variables.tryParse(jobLineSplit[2]);
                pcb.progSegSize=Variables.tryParse(jobLineSplit[3]);
                pcb.CPU_TRACE=Variables.tryParse(jobLineSplit[4]);
                if(pcb.CPU_TRACE>1 || pcb.CPU_TRACE<0){
                    //CPU TRACE HANDLER
                    ErrorHandler.throwWarning(ErrorHandler.WR_CPU_INVALID_FLAG);
                }
            }
            
            //DIVIDE DATA TO SEGMENTS
            int i=2;
            String progLine="",inpLine="",outLine="";
            for(i=2;i<lines.length;i++){
                progLine=progLine+lines[i];
            }
            
            
            lines=input.split("\\r?\\n");
            if(!lines[0].toUpperCase().equals("INPUT")){
                ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_INPUT);
            }
            for(i=1;i<lines.length;i++){
                inpLine=inpLine+lines[i];
            }
            
            lines=output.split("\\r?\\n");
            if(!lines[0].toUpperCase().equals("FIN")){
                ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_MISSING_OUTPUT);
            }
    
            for(i=0;i<pcb.outSegSize;i++){
                outLine=outLine+"0000";
            }
            
            /* if(progLine.length()!=(pcb.progSegSize*4) && progLine==null){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_INVALID_JOB_SIZE);
            }
            if(inpLine.length()!=(pcb.inpSegSize*4)&& inpLine!=null){
            ErrorHandler.throwError(ErrorHandler.ER_SPOOLER_INVALID_INPUT_SIZE);
            }*/
            
            /*
            INITIATE PAGE DIVISION
            */
            int ex0=(int) Math.ceil((double)((Math.ceil(progLine.length()/4))/(double)Variables.PAGE_SIZE));
            int ex1=(int)Math.ceil((double)((Math.ceil(inpLine.length()/4))/(double)Variables.PAGE_SIZE));
            int ex2=(int)(pcb.outSegSize/(double)Variables.PAGE_SIZE);
            pcb.noOfFrames=ex0+ex1+ex2;
            pcb.pageFaults=ex1+ex2;
            
            int lastProgPageSize=(int) ((Math.ceil(progLine.length()/4))%Variables.PAGE_SIZE);
            int lastInpPageSize=(int) ((Math.ceil(inpLine.length()/4))%Variables.PAGE_SIZE);
            int lastOutPageSize=(int) (pcb.outSegSize%Variables.PAGE_SIZE);
            
            pcb.actualCodeSize=progLine.length()/4;
            /*
            Divide Each Segment to Frames
            */
            Page[] progPages=divideSegmentToPages(progLine,ex0,lastProgPageSize);
            Page[] inpPages=divideSegmentToPages(inpLine,ex1,lastInpPageSize);
            Page[] outPages=divideSegmentToPages(outLine,ex2,lastOutPageSize);
            String frms[]=inpLine.split("(?<=\\G.{4})");
            for(int k=0;k<frms.length;k++){
                pcb.inputList.add(frms[k]);
                System.out.println(frms[k]);
            }
            pcb.progPageFrames=storeInDisk(progPages);
            pcb.inpPageFrames=storeInDisk(inpPages);
            pcb.outPageFrames=storeInDisk(outPages);
            
            //pcb.display();
            Variables.JOB_PCBS.add(pcb);
        }
    }
    public void inputSpooler(){
        
    }
    /*
    Divides every Segment to Pages
    */
    
    private Page[] divideSegmentToPages(String progLine, int ex0,int lastProgPageSize) {
        Page progPages[]=new Page[ex0];
        String frms[]=progLine.split("(?<=\\G.{4})");
        int j=0;
        int i;
        for(i=0;i<ex0-1;i++){
            String sub[]=Arrays.copyOfRange(frms, j, j+Variables.PAGE_SIZE);
            progPages[i]=new Page(sub);
            j=j+Variables.PAGE_SIZE;
        }
        if(ex0>=1){
            progPages[ex0-1]=new Page();
            for(int k=0;k<lastProgPageSize;k++){
                try{
                    progPages[ex0-1].addFrame(frms[j], k);
                    j++;
                }catch(Exception e){
                    
                    //ERROR HANDLER FRAMES VALUE GREATER THAN THIS GIVEN VALUE
                }
                
            }
        }
        return progPages;
    }
    /*
    Stores the Obtained pages into the disk
    */
    
    private ArrayList storeInDisk(Page[] inpPages) {
        ArrayList m=new ArrayList();
        for(int j=0;j<inpPages.length;j++){
            for(int i=0;i<Variables.DISK_FMBV.length;i++){
                if(Variables.DISK_FMBV[i]==0){
                    Variables._DISK[i]=inpPages[j];
                    Variables.DISK_FMBV[i]=1;
                    m.add(i);
                    break;
                }
            }
        }
        return m;
    }
    /*
    Spools output to the disk. If any dirty bit is set then the
    Page is written to the disk.
    */
    
    protected void outputSpool() throws IOException{
        PCB pcb=Variables.currentPCB;
        PMT prog=pcb.progSegment;
        PMT inp=pcb.inpSegment;
        PMT out=pcb.outSegment;
        
        for(int i=0;i<prog.pageArr.length;i++){
            if(prog.modify[i]!=0 && prog.pageArr[i]!=-1){
                int diskFrame=getDiskFrame(i);
                writePageToDisk(i,diskFrame);
            }
        }
        for(int i=0;i<inp.pageArr.length;i++){
            if(prog.modify[i]!=0&& inp.pageArr[i]!=-1){
                int diskFrame=getDiskFrame(i);
                writePageToDisk(i,diskFrame);
            }
        }
        if(out!=null){
            for(int i=0;i<out.pageArr.length;i++){
                if(prog.modify[i]!=0&& out.pageArr[i]!=-1){
                    int diskFrame=getDiskFrame(i);
                    writePageToDisk(i,diskFrame);
                }
            }
        }
        updateExecutionProfile();
    }
    /*
    Write a single page to the disk;
    */
    
    private void writePageToDisk(int pageNo,int replaceFrame) throws IOException {
        pageNo=Variables.getCurrentPMT().pageArr[pageNo];
        Page memPage=Variables._MEM[pageNo];
        Variables._DISK[replaceFrame]=memPage;
        Variables.MEM_FMBV[pageNo]=0;
    }
    
    /*
    Obtains the DISK Frame address from PCB
    */
    
    private int getDiskFrame(int pageToBeReplaced) throws IOException {
        PCB pcb=Variables.currentPCB;
        ArrayList al=null;
        if(Variables.currentSegment==0){
            al=(ArrayList) pcb.progPageFrames;
        }
        else if(Variables.currentSegment==1){
            al=(ArrayList) pcb.inpPageFrames;
        }
        else if(Variables.currentSegment==2){
            al=(ArrayList)pcb.outPageFrames;
        }
        if(al==null){
            ErrorHandler.throwError(ErrorHandler.ER_SEGMENT_NOT_FOUND);
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
    
    public void updateExecutionProfile() {
        int JOB_ID = Variables.JOB_ID;
        int TURN_AROUND_TIME, WAITING_TIME, EXECUTION_TIME, RUN_TIME;
        String ENTRY_TIME, EXIT_TIME;
        PCB pcb=Variables.currentPCB;
        List frames = pcb.progPageFrames;
        List program = pcb.progPageFrames;
        List input = pcb.inpPageFrames;
        List output = pcb.outPageFrames;
        String input_segment = "", output_segment = "";
        
        ENTRY_TIME = Integer.toHexString(pcb.entryTime);
        EXIT_TIME = Integer.toHexString(pcb.execTime);
        TURN_AROUND_TIME = pcb.entryTime - pcb.execTime;
        WAITING_TIME = pcb.PAGE_FAULT_TIME + pcb.IOTime + pcb.SEGMENT_FAULT_TIME;
        EXECUTION_TIME = pcb.execTime;
        RUN_TIME = EXECUTION_TIME + WAITING_TIME;
        
        
        if("RUNTIME WARNING: SUSPECTED INFINITE LOOP".equals(ErrorHandler.Warning)) {
            Variables.infTimeLost += EXECUTION_TIME;
        }
        
        
        //System.out.println("OUTPUT SPOOLED for: "+JOB_ID);
        for(int i=0;i<frames.size();i++) {
            int frame = (int)frames.get(0);
            Variables.MEM_FMBV[frame] = 0;
            Variables._MEM[frame] = null;
            for(int j=0;j<8;j++) {
                Variables._MEM[frame] = null;
            }
        }
        for(int i=0;i<program.size();i++) {
            Variables.DISK_FMBV[i] = 0;
            
            for(int j=0;j<8;j++) {
                Variables._DISK[j] = null;
            }
        }
        for(int i=0;i<input.size();i++) {
            int page = (int)input.get(i);
            
            Variables.DISK_FMBV[page] = 0;
            
            for(int j=0;j<8;j++) {
                Variables._DISK[j] = null;
            }
        }
        for(int i=0;i<output.size();i++) {
            int page = (int)input.get(i);
            
            Variables.DISK_FMBV[page] = 0;
            
            for(int j=0;j<8;j++) {
                Variables._DISK[j] = null;
            }
        }
        
        Variables.OUTPUT=Variables.OUTPUT+("JOB ID: "+JOB_ID);
        
        
        String frms[]=input_segment.split("\\s+");
        String temp="";
        for(int i=0;i<frms.length;i++){
            if(!"".equals(frms[i]))
                temp=temp+Integer.parseInt(frms[i],2)+"\t";
        }
        
        Variables.OUTPUT=Variables.OUTPUT+("INPUT SEGMENT DATA(DEC): "+temp);
        
        frms=output_segment.split("\\s+");
        temp="";
        for(int i=0;i<frms.length;i++){
            if(!"".equals(frms[i]))
                temp=temp+Integer.parseInt(frms[i],2)+"\t";
        }
        if(!"".equals(output_segment)) {
            Variables.OUTPUT=Variables.OUTPUT+("OUTPUT SEGMENT DATA(DEC):"+temp);
        }
        
        else {
            Variables.OUTPUT=Variables.OUTPUT+("NO OUTPUT FOUND");
        }
        
        Variables.OUTPUT=Variables.OUTPUT+("TERMINATION NATURE: "+ ErrorHandler.NATURE);
        Variables.OUTPUT=Variables.OUTPUT+("JOB ENTRY TIME (HEX): "+ENTRY_TIME.toUpperCase() + "\nJOB EXIT TIME(HEX): "+ EXIT_TIME.toUpperCase());
        Variables.OUTPUT=Variables.OUTPUT+("JOB RUN TIME (DEC): "+ (RUN_TIME));
        Variables.OUTPUT=Variables.OUTPUT+("JOB EXECUTION TIME(DEC) "+(EXECUTION_TIME)+"\nJOB IO TIME(DEC): "+pcb.IOTime);
        Variables.OUTPUT=Variables.OUTPUT+("JOB PAGE FAULT TIME(DEC): "+pcb.PAGE_FAULT_TIME+"\nJOB SEGEMENT FAULT TIME(DEC): "+pcb.SEGMENT_FAULT_TIME);
        Variables.OUTPUT=Variables.OUTPUT+("JOB TURN AROUND TIME(DEC): "+(pcb.turnAroundTime)+"\nJOB WAITING TIME(DEC): "+(WAITING_TIME));
        Variables.OUTPUT=Variables.OUTPUT+("JOB PAGE FAULTS: "+ pcb.pageFaults);
        Variables.OUTPUT=Variables.OUTPUT+("\n######################################################################");
    }
    
    public static void Utilize() {
        int memory_used,disk_used;
        memory_used = Variables.mem_size;
        disk_used = Variables.disk_size;
        
        Variables.OUTPUT=Variables.OUTPUT+("AT TIME INTERVAL: "+ Variables._CLOCK);
        Variables.OUTPUT=Variables.OUTPUT+("READY QUEUE(JOB NOs) : ");
        if(Variables.readyQueue.isEmpty()){
            Variables.OUTPUT=Variables.OUTPUT+("EMPTY");
        }
        else{
            Iterator itr=Variables.readyQueue.iterator();
            while(itr.hasNext()){
                Variables.OUTPUT=Variables.OUTPUT+(itr.next() + ", ");
            }
        }
        Variables.OUTPUT=Variables.OUTPUT+("CURRENT JOB NO: "+ Variables.JOB_ID);
        Variables.OUTPUT=Variables.OUTPUT+("BLOCKED QUEUE(JOB NOs) :");
        if(Variables.blockedQueue.isEmpty()){
            Variables.OUTPUT=Variables.OUTPUT+("EMPTY");
        }
        else{
            Iterator itr=Variables.blockedQueue.iterator();
            while(itr.hasNext()){
                Variables.OUTPUT=Variables.OUTPUT+(itr.next() + ", ");
            }
        }
        Variables.OUTPUT=Variables.OUTPUT+("MEMORY UTILIZATION (PAGES): "+ memory_used+"/32");
        Variables.OUTPUT=Variables.OUTPUT+("MEMORY UTILIZATION (FRAMES): "+ memory_used*8+"/256");
        Variables.OUTPUT=Variables.OUTPUT+("DISK UTILIZATION (PAGES): "+ disk_used+"/256");
        Variables.OUTPUT=Variables.OUTPUT+("MEMORY UTILIZATION (FRAMES): "+ disk_used*8+"/2048");
        Variables.OUTPUT=Variables.OUTPUT+("\n######################################################################");
    }
    
    public static void Info() {
        Metering();
        Variables.OUTPUT=Variables.OUTPUT+("JOBS TERMINATED NORMALLY: "+Variables.normalCount);
        Variables.OUTPUT=Variables.OUTPUT+("JOBS TERMINATED ABNORMALLY: "+Variables.abnormalCount);
        Variables.OUTPUT=Variables.OUTPUT+("TIME LOST DUE TO ABNORMALLY TERMINATED JOBS: "+Variables.abnormalTimeLost);
        Variables.OUTPUT=Variables.OUTPUT+("TIME LOST DUE TO SUSPECTED INFINITE JOBS: "+Variables.infTimeLost);
        if(Variables.infiniteJobs.equals("")){
            Variables.OUTPUT=Variables.OUTPUT+("IDs OF INFINITE JOBS: NONE");
        }
        else{
            Variables.OUTPUT=Variables.OUTPUT+("IDs OF INFINITE JOBS: "+Variables.infiniteJobs);
        }
        Variables.OUTPUT=Variables.OUTPUT+("MEAN TURN AROUND TIME (TERMINATED NORMALLY): "+Variables.meanTA);
        Variables.OUTPUT=Variables.OUTPUT+("MEAN WAITING TIME (TERMINATED NORMALLY): "+ Variables.meanWA);
        Variables.OUTPUT=Variables.OUTPUT+("MEAN PAGE FAULTS: \n"+Variables.meanPF);
    }
    
    public static void Metering() {
        Variables.OUTPUT=Variables.OUTPUT+("#################METERING AND REPORTING##########################");
        Variables.OUTPUT=Variables.OUTPUT+("TOTAL JOBS PROCESSED: "+ Variables.JOBS_PROCESSED);
        Variables.OUTPUT=Variables.OUTPUT+("CPU EXECUTION TIME: ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.RUN_TIMES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.RUN_TIMES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.RUN_TIMES));
        Variables.OUTPUT=Variables.OUTPUT+("TURN AROUND TIME: ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.TATIMES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.TATIMES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.TATIMES));
        Variables.OUTPUT=Variables.OUTPUT+("CODE SEGMENT SIZE (GIVEN): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.PROGSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.PROGSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.PROGSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("CODE SEGMENT SIZES (USED): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.LOADSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.LOADSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.LOADSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("INPUT SEGMENT SIZES (GIVEN): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.INPUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.INPUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.INPUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("INPUT SEGMENT SIZES (USED): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.LOADINPSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.LOADINPSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.LOADINPSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("OUTPUT SEGMENT SIZES (GIVEN): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.OUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.OUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.OUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("OUTPUT SEGMENT SIZES (USED): ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.LOADOUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.LOADOUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.LOADOUTSIZES));
        Variables.OUTPUT=Variables.OUTPUT+("CPU SHOTS: ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: "+Min(Variables.CPU_SHOTS));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: "+Max(Variables.CPU_SHOTS));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: "+Average(Variables.CPU_SHOTS));
        Variables.OUTPUT=Variables.OUTPUT+("IO REQUESTS: ");
        Variables.OUTPUT=Variables.OUTPUT+("MINIMUM: %.2f\n"+Min(Variables.IO_REQS));
        Variables.OUTPUT=Variables.OUTPUT+("MAXIMUM: %.2f\n"+Max(Variables.IO_REQS));
        Variables.OUTPUT=Variables.OUTPUT+("AVERAGE: %.2f\n"+Average(Variables.IO_REQS));
        Variables.OUTPUT=Variables.OUTPUT+("END TIME CLOCK (DEC): "+ Variables._CLOCK+"\n");
        
//        Variables.OUTPUT=Variables.OUTPUT+("\n######################################################################");
    }
    
    public static double Min(ArrayList<ArrayList<Integer>> input) {
        int minimum = -1;
        int minimum_position = -1;
        for(int i=0;i<input.size();i++) {
            int value = input.get(i).get(1);
            
            if(minimum == -1) {
                minimum = value;
                minimum_position = i;
            }
            
            else {
                if(value < minimum) {
                    minimum = value;
                    minimum_position = i;
                }
            }
        }
        
        return minimum;
    }
    
    public static double Max(ArrayList<ArrayList<Integer>> input) {
        int maximum = -1;
        int maximum_position = -1;
        
        for(int i=0;i<input.size();i++) {
            int value = input.get(i).get(1);
            
            if(maximum == -1) {
                maximum = value;
                maximum_position = i;
            }
            
            else {
                if(value > maximum) {
                    maximum = value;
                    maximum_position = i;
                }
            }
        }
        
        return maximum;
    }
    
    public static double Average(ArrayList<ArrayList<Integer>> input) {
        double average = 0;
        
        for(int i=0;i<input.size();i++) {
            average += input.get(i).get(1);
        }
        
        average = average/input.size();
        average = Math.round(average * 100.0)/100.0;
        
        return average;
    }
    
    public static int Rand(int x){
        int i=(int)((double)Math.random()*x);
        return i;
    }
    
}
