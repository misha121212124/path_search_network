import java.util.Random;

public class Figures {

    public static double[][] getLine(int x, int y){
        double f[][] = new double[x][y];
        for(int i = 0; i<x; i++) {
            for (int j = 0; j < y; j++) {
                f[i][j]=0;
            }
        }
        return f;
    }

    public static double[][] getAngle(int x, int y){
        double f[][] = new double[x][y];
        for(int i = 0; i<x; i++) {
            for (int j = 0; j < y; j++) {
                f[i][j]=2;
            }
        }
        Random r = new Random();
        switch (r.nextInt(4)){
            case 0:
                for(int i = 0; i<x; i++) {
                    f[i][0]=0;
                }
                    break;
            case 1:
                for(int i = 0; i<x; i++) {
                    f[i][y-1]=0;
                }
                break;
            case 2:
                for (int j = 0; j < y; j++) {
                    f[0][j]=0;
                }
                break;
            case 3:
                for (int j = 0; j < y; j++) {
                    f[x-1][j]=0;
                }
                break;
        }

        return f;
    }

    public static double[][] getSqare(int x, int y){
        double f[][] = new double[x][y];
        for(int i = 0; i<x; i++) {
            for (int j = 0; j < y; j++) {
                if(i==x||i==0||j==0||j==y){
                    f[i][j]=0;
                }else{
                    f[i][j]=2;
                }

            }
        }
        if(x+y > 4 && x>1 && y>1 ){
            Random r = new Random();
            for(int i = 0; i<r.nextInt((int)(1.5*(x+y)))+1; i++){
                int p = r.nextInt((x+y)*2-8);
                if(p<x-2){
                    f[p+1][0]=2;
                }
                else{
                    p-=x-2;
                    if(p < y-2){
                        f[x-1][p+1] = 2;
                    }else{
                        p-=y-2;
                        if(p < x-2){
                            f[p+1][y-1] = 2;
                        }else{
                            p -= x-2;
                            if(p < y-2){
                                f[0][p+1] = 2;
                            }
                        }
                    }
                }

            }
        }

        return f;
    }

    public static double[][] getRandom(){
        Random r = new Random();
        int l = r.nextInt(4)+1;
        int a = r.nextInt(3);
        int s = r.nextInt(2);

        int rand = r.nextInt(l+a+s);
        int x,y;
        if(rand<l){
            x = r.nextInt(6)+2;
            y = r.nextInt(6)+2;
            if(x>y)y/=3;
            else x/=3;
            x=(x>0)?x:1;
            y=(y>0)?y:1;
            return getLine(x,y);
        }
        else if(rand<l+a) {
            x = r.nextInt(4)+1;
            y = r.nextInt(4)+1;
            x=(x>0)?x:1;
            y=(y>0)?y:1;
            return getAngle(x,y);
        }
        else {
            x = r.nextInt(5)+2;
            y = r.nextInt(5)+2;
            x=(x>0)?x:2;
            y=(y>0)?y:2;
            return getSqare(x,y);
        }

    }

}
