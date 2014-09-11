
import java.io.FileWriter;
import java.io.IOException;

/*
f.All the Errors and Warining are given Different integer values.
The Error Handler catches the Errors and displays the Reason for errors.
Most compile time and Runtime errors are caught here

g. Error handler is given a Separate class in order for modularity. For code Understandability
I preffered this.
*/

public class ErrorHandler {
    /*
    ERROR CODES
    */
    
    public final static int ER_LOADER_INVALID_FORMAT=1;
    public final static int WR_LOADER_INVALID_FLAG=2;
    public final static int ER_LOADER_INVALID_IPC=3;
    public final static int ER_LOADER_INVALID_SIZE=4;
    
    public final static int ER_STACK_OVERFLOW=11;
    public final static int ER_STACK_UNDERFLOW=12;
    
    public final static int ER_MEM_OUT_OF_RANGE=21;
    
    public final static int ER_ARI_DIVIDE_BY_ZERO=31;
    public final static int WR_INF_LOOP=101;
    public static final int ER_CPU_INVALID_OPCODE=41;
    public final static int WR_CPU_INVALID_FLAG=42;
    public final static int WR_VALUE_OUT_OF_RANGE=51;
    
    public final static int ER_PAGE_NOT_FOUND=61;
    public final static int ER_NO_PMT_FOUND=62;
    
    public final static int ER_SEGMENT_NOT_FOUND=71;
    public static int NATURE=1;
    
    
    public static String Error="";
    public static String stack_trace="";
    public static String Warning="";
    public final static int ER_SPOOLER_MISSING_JOB=81;
    public final static int ER_SPOOLER_MISSING_INPUT=82;
    public final static int ER_SPOOLER_INVALID_JOB_SIZE=83;
    public final static int ER_SPOOLER_INVALID_INPUT_SIZE=84;
    public final static int ER_FILE_NOT_FOUND=91;
    public final static int ER_SPOOLER_MISSING_OUTPUT=85;
    
    public final static int WR_LOADER_INVALID_INPUTS=101;
    public final static int WR_MULTIPLE_FIN=102;
    public final static int WR_INVALID_OPERAND=103;
    public final static int WR_INF_LOOPs=104;
    public final static int ER_DATA_OUT_OF_RANGE=150;
    
    
    
    /*
    Warnings are Caught and the program execution completes but a Warning
    Appears on the output file
    */
    
    public static void throwWarning(int err) {
        Warning=Warning+"WARNINGS\n";
        switch(err){
            case WR_CPU_INVALID_FLAG: Warning=Warning+"RUNTIME WARNING : BAD TRACE FLAG";
            break;
            case WR_LOADER_INVALID_INPUTS:Warning=Warning+"SPOOLER WARNING: MISMATCH OF NUMBER OF INPUTS\n";
            break;
            case WR_MULTIPLE_FIN:Warning=Warning+"SPOOLER WARNING: MULTIPLE FIN LABELS";
            break;
            case WR_INVALID_OPERAND:Warning=Warning+"RUNTIME WARNING: INVALID OPERAND";
            break;
            case WR_INF_LOOPs:Warning=Warning+"RUNTIME WARNING: SUSPECTED INFINITE LOOP\n";
            break;
                
        }
//        SwitcherDispatcher.releaseResources();
        // SwitcherDispatcher.startNewJob();
        Warning=Warning+"\n";
    }
    
    /*
    Erros are Caught and the program execution terminates with abnormal behaviour
    Error Appears on the output file. A Stack Trace is also shown if any syntax errors
    */
    
    public static void throwError(int err) throws IOException{
        Error=Error+"ERRORS :\n";
        switch(err){
            case ER_LOADER_INVALID_FORMAT: Error=Error+"SPOOLER ERROR: INVALID LOADER FORMAT";
            NATURE=0;
            break;
            case ER_LOADER_INVALID_IPC : Error=Error+"Error : INVALID LOADER IPC";
            NATURE=0;
            break;
            case ER_LOADER_INVALID_SIZE : Error=Error+"Error : INVALID LOADER SIZE VALUE";
            NATURE=0;
            break;
            case ER_STACK_OVERFLOW:Error=Error+"RUNTIME ERROR: STACK OVERFLOW";
            NATURE=0;
            break;
            case ER_STACK_UNDERFLOW:Error=Error+"RUNTIME ERROR: STACK UNDERFLOW";
            NATURE=0;
            break;
            case ER_MEM_OUT_OF_RANGE:Error=Error+"RUNTIME ERROR: MEMEORY ADDRESS OUT OF RANGE";
            NATURE=0;
            break;
            case ER_DATA_OUT_OF_RANGE:Error=Error+"RUNTIME ERROR: DATA OUT OF RANGE. DATA SHOULD BE BETWEEN -32768 AND 32767";
            NATURE=0;
            break;
            case ER_ARI_DIVIDE_BY_ZERO:Error=Error+"RUNTIME ERROR: DIVIDE BY ZERO";
            NATURE=0;
            break;
            case ER_CPU_INVALID_OPCODE: Error=Error+"RUNTIME ERROR: INVALID OPCODE";
            NATURE=0;
            break;
            case ER_NO_PMT_FOUND: Error=Error+"Error : PMT NOT FOUND";
            NATURE=0;
            break;
            case ER_PAGE_NOT_FOUND: Error=Error+"Error : PAGE NOT FOUND";
            NATURE=0;
            break;
            case ER_SEGMENT_NOT_FOUND: Error=Error+"Error : SEGMENT NOT FOUND";
            NATURE=0;
            break;
            case ER_SPOOLER_MISSING_JOB: Error=Error+"SPOOLER ERROR: MISSING JOB LABEL";
            NATURE=0;
            writeToCompilerErrors();
            
            break;
            case ER_SPOOLER_MISSING_INPUT: Error=Error+"SPOOLER ERROR: MISSING INPUT LABEL";
            NATURE=0;
            writeToCompilerErrors();
            System.exit(0);
            
            break;
            case ER_SPOOLER_MISSING_OUTPUT: Error=Error+"SPOOLER ERROR: MISSING FIN LABEL";
            NATURE=0;
            writeToCompilerErrors();
            System.exit(0);
            
            break;
            case ER_SPOOLER_INVALID_INPUT_SIZE: Error=Error+"INPUT ERROR: INVALID INPUT";
            NATURE=0;
            writeToCompilerErrors();
            
            
            break;
            case ER_SPOOLER_INVALID_JOB_SIZE: Error=Error+"Error : INVALID JOB SIZE";
            NATURE=0;
            writeToCompilerErrors();
            
            
            break;
            case ER_FILE_NOT_FOUND: Error=Error+"Error : FILE NOT FOUND";
            NATURE=0;
            writeToCompilerErrors();
            break;
        }
        Error=Error+"\n";
    }
    
    /*
    Prints Stack Trace when there is an Error
    */
    
    static String PrintStackTrace() {
        stack_trace=stack_trace+"STACK TRACE\n";
        StackTraceElement[] st=Thread.currentThread().getStackTrace();
        for(int i=1;i<st.length;i++){
            stack_trace=stack_trace+"Class Name :"+st[i].getFileName()+" Method Name :"+st[i].getMethodName()+" Line Number:"+st[i].getLineNumber()+"\n";
        }
        return stack_trace;
    }
    
    /*
    The Error Check Methods are also declared here. These are declared static
    So these can be used in all the classes in the simulation
    Checks the Loader format. If Loader has characters like z,x which are not hex values
    Error is returned.
    */
    
    /*
    Checks the address if Memory address thats being asked to check is greater
    than 256 or less than 0 error is caught
    */
    
    public static void checkMemoryAdressBound(int i) throws IOException {
        if(i>=256 | i<0){
            ErrorHandler.throwError(ErrorHandler.ER_MEM_OUT_OF_RANGE);
        }
    }
    /*
    Compiler Errors are Handled here
    */
    private static void writeToCompilerErrors() throws IOException {
        FileWriter fw=new FileWriter("./output_file.txt",true);
        String temp="JOB ID :\n";
        if(!"".equals(ErrorHandler.Error)){
            temp=temp+ErrorHandler.Error+"\n";
            temp=temp+ErrorHandler.stack_trace+"\n";
        }
        if(!"".equals(ErrorHandler.Warning)){
            temp=temp+ErrorHandler.Warning+"\n";
        }
        if(ErrorHandler.NATURE==1){
            temp=temp+"NORMAL EXECUTION\n";
        }
        if(ErrorHandler.NATURE==0){
            temp=temp+"ABNORMAL EXECUTION\n\n";
            temp=temp+"\n";
        }
        // fw.write(temp);
        fw.close();
        //  System.exit(0);
        
    }
    
}
