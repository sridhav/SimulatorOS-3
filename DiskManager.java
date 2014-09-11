import java.io.IOException;
/*
    f. Disk Manager contains some functions managing the disk
       

*/

public class DiskManager {
    public static Page[] DISK=new Page[256];
    public static boolean[] DISK_FMBV=new boolean[256];
    /*
        Checks for Free DISK FMBV
    */
    public static int checkFreeFMBV(){
        for(int i=0;i<256;i++){
            if(!DISK_FMBV[i]){
                return i;
            }
        }
        //No Disk Space Available.
        return 0;
    }
    /*
        Adds Page to Disk
    */
    public static int setPageToDisk(Page page){
        int i=checkFreeFMBV();
        DISK[i]=page;
        DISK_FMBV[i]=true;
        return i;
    }
    /*
        Debug Method
    */
    public static void display(){
        for(int i=0;i<DISK.length;i++){
            if(DISK_FMBV[i]){
                System.out.println(i);
                DISK[i].display();
            }
        }
    }
    /*
        Removes a Page from disk
    */
    static void removePage(int i) {
        DISK_FMBV[i]=false;
        Page pg=new Page();
        DISK[i]=pg;
    }
    
    /*
    returns Page From DISK
    */
    public static Page getPageFromDisk(int pageNo){
        Page pg=null;
        if(Variables.currentSegment==0){
            int diskPageAddr=(int) Variables.currentPCB.progPageFrames.get(pageNo);
            pg=DiskManager.DISK[diskPageAddr];
        }
        else if(Variables.currentSegment==1){
            int diskPageAddr=(int) Variables.currentPCB.inpPageFrames.get(pageNo);
            pg=DiskManager.DISK[diskPageAddr];
        }
        else if(Variables.currentSegment==2){
            int diskPageAddr=(int) Variables.currentPCB.outPageFrames.get(pageNo);
            pg=DiskManager.DISK[diskPageAddr];
        }
        if(pg==null){
            
       }
        return pg;
    }
}
