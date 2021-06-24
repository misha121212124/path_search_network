import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.UnaryOperator;

public class GraphicForm extends JFrame implements Runnable, MouseListener {

    private final int w = 600;
    private final int h = 600;
    public final int delimeter = 10;
    private final int sXLength = w/delimeter;
    private final int sYLength = h/delimeter;

    private ArrayList<double[][]> mapList = new ArrayList<>();
    int mapIndex=0;
    private double[][] map;// = new double[][]{
//            {2,2,2,2,2,2,2,2,2,2},
//            {2,2,0,0,0,0,0,2,2,2},
//            {2,2,2,2,2,2,2,2,2,2},
//            {2,2,0,2,2,2,2,2,2,2},
//            {2,2,0,2,2,3,2,2,2,2},
//            {2,2,0,2,2,2,2,2,2,2},
//            {2,2,2,2,2,2,2,2,2,2},
//            {2,2,2,2,2,2,2,0,2,2},
//            {2,2,2,2,2,2,0,0,2,2},
//            {2,2,2,2,2,2,2,2,2,2}
//    };

    private int[][] way;



    private BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    private BufferedImage pimg = new BufferedImage(w / sXLength, h / sYLength, BufferedImage.TYPE_INT_RGB);
    UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
    UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
    private int frame = 0;

    private NeuralNetwork nn;

    public GraphicForm() {
        generateNewMap();
        mapList.add(map);
        //way = findWayMap();


//        nn = new NeuralNetwork(0.014, sigmoid, dsigmoid,100,35,13, 4);
//        nn = new NeuralNetwork(0.014, sigmoid, dsigmoid,
//                delimeter*delimeter, delimeter*4, delimeter+delimeter/3, 4);
//
        nn = new NeuralNetwork(0.001, sigmoid, dsigmoid,
                delimeter*delimeter,  (int) (Math.log10(delimeter)*delimeter*2)+2, (delimeter*3)/2, 4);

        this.setSize(w + 16, h + 38);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        this.add(new JLabel(new ImageIcon(img)));
        addMouseListener(this);
    }

    public void generateNewMap(){
        double[][] newMap = new double[delimeter][delimeter];
        for(int i = 0; i<delimeter; i++) {
            for (int j = 0; j < delimeter; j++) {
                newMap[i][j]=2;
            }
        }
        Random r = new Random();
        int x,y, count=r.nextInt(delimeter/2)+delimeter/2;// зробити 3 і 2 !!!!!!!!!!!!!!!!!!!
        for (int k = 0; k<count; k++){
            x = r.nextInt(delimeter);
            y = r.nextInt(delimeter);
            double f[][] = Figures.getRandom();
            for(int i = x; i<delimeter; i++) {
                for (int j = y; j < delimeter; j++) {
                    if(i-x>=f.length||j-y>=f[0].length)continue;
                    newMap[i][j]=f[i-x][j-y];
                }
            }
        }
        map = newMap;
        int goal[] = getRandomStart();
        newMap[goal[0]][goal[1]]=3;
        map = newMap;
        way = findWayMap();

    }

    private int counter = 0;


    @Override
    public void run() {
        class MyThread extends Thread{
            int k=0;
            @Override
            public void run(){
                while (true) {
                    for (k = 0; k < 500; k++) {
                        int[] start = getRandomStart();
                        train(start[0], start[1]);
                    }
//                    System.out.println("000");
                    try { Thread.sleep(5); } catch (InterruptedException e) {}

                }
            }
        }
        MyThread thread = new MyThread();
//        for (int k = 0; k<10; k++){
//            threads[k] = new MyThread();
            thread.start();
//        }
        class MapGenerator extends Thread{
            @Override
            public void run(){
                while (true) {
                    try { Thread.sleep(15000); } catch (InterruptedException e) {}
                    generateNewMap();
                }
            }
        }

        MapGenerator generator = new MapGenerator();

        //generator.start();
        int frameRate = 5;
        while (true) {
            this.repaint();
            try { Thread.sleep(1000/frameRate); } catch (InterruptedException e) {}

//            for (int k = 0; k < 100; k++) {
//                int [] start = getRandomStart();
//                train(start[0],start[1]);
//            }
        }
    }
//ArrayList<Thread> list;

    @Override
    public void paint(Graphics g) {

        counter++;

//        for (int k = 0; k < 50; k++) {
//            int [] start = getRandomStart();
//            train(start[0],start[1]);
//        }
        // малюємо мапу
        for(int i = 0; i<delimeter; i++){
            for (int j = 0; j <delimeter ; j++) {
                Color color;
                if(map[i][j]<0.5)
                    color= new Color(200,200,200);
                else if(map[i][j]<2.5)
                    color= new Color(0,255,0);
                else color= new Color(255,0,0);
                pimg.setRGB(i, j, color.getRGB());
            }
        }

        Graphics ig = img.getGraphics();
        ig.drawImage(pimg, 0, 0, w, h, this);

        // робота мережі
        for(int i = 0; i<delimeter; i++) {
            for (int j = 0; j < delimeter; j++) {
                if(map[i][j]<0.5 || map[i][j]>2.5)
                    continue;
                //Layer[] layers = nn.feedForward(getInputs(i,j));
                //BestResult result = new BestResult(layers[layers.length - 1].neurons);
                BestResult result = new BestResult(nn.feedForward(getInputs(i,j)));
                // малюємо стрілку, залежно від результатів мережі
                result.drowArrow(ig,i*sXLength+300/delimeter, j*sYLength+300/delimeter,
                        100/delimeter,100/delimeter, i,j,this);

            }
        }

//        System.out.print(counter+" ");
//        try { Thread.sleep(100); } catch (InterruptedException e) {}

//        if(counter % 50==0){
//            ig.setFont(new Font("TimesRoman", Font.PLAIN, 50));
//            ig.setColor(Color.RED);
//            ig.drawString("Пауза", 50,50);
//            g.drawImage(img, 8, 30, w, h, this);
////            System.out.println("\nnew map");
////
////            generateNewMap();
////
////            System.out.println("generated");
//
////            try {
////                System.in.read();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            try { Thread.sleep(500); } catch (InterruptedException e) {}
//        }else {
        ig.setFont(new Font("TimesRoman", Font.PLAIN, 15));
        ig.setColor(Color.BLUE);
        ig.drawString("Розробник: Муляр Михайло Андрійович", 20,20);
        g.drawImage(img, 8, 30, w, h, this);
//        }
        frame++;

    }
    // проходимо по мережі і використовуємо backPropagation
    private void train(int x, int y){
        nn.feedForward(getInputs(x, y));
        //double[] targets = new double[4];
        //int best=getBestDess(start[0],start[1]);
        double[] targets = getBestDess(x, y);
        //маємо null якщо з точки не існує шляху, тоді не проводимо навчання
        if(targets!=null)
        nn.backpropagation(targets);
//        nn.feedForward(getInputs(x,y));

    }

    // формуємо масив входів для мережі
    private double[] getInputs(int x, int y){
        double inp[] = new double[map.length*map[0].length];
        for (int i = 0; i < delimeter; i++) {
            for (int j = 0; j < delimeter; j++) {
                if(i!=x||j!=y)
                    inp[j*delimeter+i]=map[i][j]/3;
                else inp[j*delimeter+i]=1.0/3;
                //System.out.printf("%4f", inp[j*delimeter+i]);
            }
            //System.out.println();
        }
        return  inp;
    }

    // шукаємо випадкову точку для навчання мережі (не стіна і не фініш)
    private int[] getRandomStart(){
        Random r = new Random();
        int x = r.nextInt(delimeter);
        int y = r.nextInt(delimeter);
        if(map[x][y]>0.5&&map[x][y]<2.5)
            return new int[]{x,y};
        else return getRandomStart();
    }
    //альтернативна стрілка :)
    public int getAngle(int x, int y){
        if(x==0){
            if(y==0)return -1;
            if(y>0)return 90;
            else return 270;
        }
        if(y==0){
            if(x>0)return 0;
            else return 180;
        }
        if(x>0)x=0;
        else x=180;
        if(y>0)y=90;
        else y=270;
        if(x != 0 || y!=270)return (x+y)/2;
        else return 315;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override // обробляємо сигнали миші
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getButton());
        if(e.getButton() == 3)
            map[getXCenter( e.getX() - 8 )/sXLength][getYCenter( e.getY() -30  )/sXLength]=2;
        else if(e.getButton() == 5) {
            if(mapList.size()>mapIndex+1){
                mapIndex++;
                map=mapList.get(mapIndex);

            }else{
                generateNewMap();
                mapList.add(map);
                mapIndex=mapList.size()-1;
                return;
            }
        }else if(e.getButton() == 4) {
            if(mapIndex-1>=0){
                mapIndex--;
                map=mapList.get(mapIndex);
            }else{
                generateNewMap();
                mapList.add(map);
                mapIndex=mapList.size()-1;
                return;
            }
        }else{
            map[getXCenter( e.getX()  - 8 )/sXLength][getYCenter( e.getY() -30  )/sXLength]=0;
        }
        way = findWayMap();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    // наступні 2-а методи
    // визначаємо, на яку клітинку натснула миша
    public int getXCenter(int x){
        x = sXLength * ( x / sXLength ) + sXLength/2;// - ((x % sXLength > sXLength / 2)? sXLength:0);;
        //System.out.print(x+" ");
        return x;
    }

    public int getYCenter(int y){
        y = sYLength * (y / sYLength ) + sYLength/2;// + ((y % sYLength > sYLength / 2)? sYLength:0);
        System.out.println(y);
        return y;
    }

    // алгоритм побудови карти відстаней до цільової точки
    private int[][] findWayMap() {
        int[][] way = new int[map.length][map[0].length];
        int[][] map = new int[way.length][way[0].length];
        for (int i = 0; i < delimeter; i++) {
            for (int j = 0; j < delimeter; j++) {
                map[i][j]=(int)Math.round(this.map[i][j]);
                way[i][j]=-1;
            }
        }
        int x=5, y=5;
        for (int i = 0; i < delimeter; i++) {
            for (int j = 0; j < delimeter; j++) {
                if(map[i][j]==3){
                    x=i;
                    y=j;
                }
            }
        }

        way[x][y]=0;
        ArrayList<int[]> list = xx(way,map,x,y);
        if(list==null)return way;
        while (list.size()>0){
            ArrayList<int[]> list2 = new ArrayList<>();
            for (int[] xy:list) {
                ArrayList<int[]> newList;
                newList = xx(way,map,xy[0],xy[1]);
                if(newList!=null)list2.addAll(newList);
            }
            list=list2;
        }
        // вивід оптимального шляху та мапи
//        for (int j = 0; j < delimeter; j++) {
//            for (int i = 0; i < delimeter; i++) {
//                System.out.printf("%3d",way[i][j]);
//            }
//            System.out.print("\t\t");
//            for (int i = 0; i < delimeter; i++) {
//                System.out.printf("%3d",map[i][j]);
//            }
//            System.out.println();
//        }

        return way;
    }

    // не придумав назву :) використовується для розширення карти відстаней
    private ArrayList<int[]> xx(int way[][], int map[][], int x, int y){//навколо точки x,y визначє відстань до цільової точки
        ArrayList<int[]>list = new ArrayList<>();
        int [] newCoor;
        int dx=1, dy=0;
        newCoor=nextStep(way,map,x,y,dx,dy);
        if(newCoor!=null)list.add(newCoor);

        dx=-1;
        newCoor=nextStep(way,map,x,y,dx,dy);
        if(newCoor!=null)list.add(newCoor);

        dx=0;
        dy=1;
        newCoor=nextStep(way,map,x,y,dx,dy);
        if(newCoor!=null)list.add(newCoor);

        dy=-1;
        newCoor=nextStep(way,map,x,y,dx,dy);
        if(newCoor!=null)list.add(newCoor);

        if(list.size()>0)return list;

        return null;
    }

    // помічаємо точку як знайдену
    private int[] nextStep(int[][] way, int[][] map, int x, int y, int dx, int dy){
        if(x+dx>=delimeter||x+dx<0) return null;
        if(y+dy>=delimeter||y+dy<0) return null;

        if ((way[x+dx][y+dy]==-1||way[x+dx][y+dy]>way[x][y]+1)&&map[x+dx][y+dy]!=0){
            way[x+dx][y+dy]=way[x][y]+1;
            return new int[]{x+dx,y+dy};
        }
        return null;
    }

    // шукаємо користь переходу із однієї клітинки в іншу (зменшення відстані)
    public int getDiff(int x, int y, int xn, int yn){
        int d = way[x][y];
        int dn;
        try{
            dn = way[xn][yn];
        }catch (ArrayIndexOutOfBoundsException ex){
            dn = -1;
        }

        if( dn < 0) return -1;
        return d-dn;
    }

    // обираємо оптимальний маршрут (один із них)
    private double[] getBestDess(int x, int y){

        BestResult best = new BestResult(1,0,0,0);
        int d=getDiff(x,y,x+1,y);

        int dn = getDiff(x,y,x+1,y+1);
        if(dn>d){
            d=dn;
            best = new BestResult(1,0,1,0);
        }

        dn = getDiff(x,y,x,y+1);
        if(dn>d){
            d=dn;
            best = new BestResult(0,0,1,0);
        }

        dn = getDiff(x,y,x-1,y+1);
        if(dn>d){
            d=dn;
            best = new BestResult(0,1,1,0);
        }

        dn = getDiff(x,y,x-1,y);
        if(dn>d){
            d=dn;
            best = new BestResult(0,1,0,0);
        }

        dn = getDiff(x,y,x-1,y-1);
        if(dn>d){
            d=dn;
            best = new BestResult(0,1,0,1);
        }

        dn = getDiff(x,y,x,y-1);
        if(dn>d){
            d=dn;
            best = new BestResult(0,0,0,1);
        }

        dn = getDiff(x,y,x+1,y-1);
        if(dn>d){
            d = dn;
            best = new BestResult(1,0,0,1);
        }
        if (d>0)return best.result;
        else
            return null;
    }

    // передає дані про мінімальну відстань з точки до цілі
    public int valueOnStep(int x, int y){
        x=(x<0)?0:(x>=delimeter)?delimeter-1:x;
        y=(y<0)?0:(y>=delimeter)?delimeter-1:y;
        return way[x][y];
    }

    // повертає розмір карти
    public int getDelimeter(){
        return delimeter;
    }


}

// трактує результат мережі, налаштовує напрям стрілки
class BestResult{
    double result[];

    //yd внизу y БІЛЬШИЙ!!!!!!!
    public BestResult(int xr, int xl, int yd, int yu) {
        result = new double[]{xr,xl,yd,yu};
    }

    public BestResult(double outputs[]) {
        result = outputs;
    }

//    public void drowArrow(Graphics g, int x, int y, int d, int h){
//
////        System.out.println(result[0]+" "+result[1]+" "+result[2]+" "+result[3]+" ");
//        int dx = getX()*200, dy=getY()*20;
//        DrowArrow.drawArrowLine(g,x-dx,y-dy,x+dx,y+dy,d, h);
//    }

    public void drowArrow(Graphics g, int x, int y, int d, int h, int i, int j, GraphicForm form){
        int max = getMax(form, i,j);
        int x1 = getX(), y1 = getY(), dis = form.getDiff(i,j,i+x1, j+y1);

        if(max==dis || (form.valueOnStep(i+x1, j+y1)>0 &&
                form.valueOnStep(i+x1, j+y1)<form.valueOnStep(i,j)))g.setColor(new Color(0,150,20));
        else if(form.valueOnStep(i,j)==form.valueOnStep(i+x1, j+y1))
            g.setColor(Color.YELLOW);
        else g.setColor(new Color(255,100,100));
        ////
//        drowArrow(g,  x,  y,  d,  h);
        int dx = getX()*200/form.getDelimeter(), dy=getY()*200/form.getDelimeter();
        DrowArrow.drawArrowLine(g,x-dx,y-dy,x+dx,y+dy,d, h, (form.getDelimeter()<15)?true:false);
    }

    private int getMax(GraphicForm form, int x, int y){
        int d=form.getDiff(x,y,x+1,y);

        int dn = form.getDiff(x,y,x+1,y+1);
        if(dn>d) d=dn;

        dn = form.getDiff(x,y,x,y+1);
        if(dn>d) d=dn;


        dn = form.getDiff(x,y,x-1,y+1);
        if(dn>d) d=dn;


        dn = form.getDiff(x,y,x-1,y);
        if(dn>d) d=dn;


        dn = form.getDiff(x,y,x-1,y-1);
        if(dn>d) d=dn;


        dn = form.getDiff(x,y,x,y-1);
        if(dn>d) d=dn;


        dn = form.getDiff(x,y,x+1,y-1);
        if(dn>d) d=dn;
        return d;
    }

    private int getX(){
        int x=0;
        if(result[0]>result[1]){
            if(result[0]>0.5)
                x=1;
        }else{
            if(result[1]>0.5)
                x=-1;
        }
        return x;
    }

    private int getY(){
        int y=0;
        if(result[2]>result[3]){
            if(result[2]>0.5)
                y=1;
        }else{
            if(result[3]>0.5)
                y=-1;
        }
        return y;
    }

    public double[] getResult() {
        return result;
    }
}

// будує стрілку
class DrowArrow{
    public static void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h, boolean bold) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        x1+=dx/20;
        y1+=dy/20;
        x2-=dx/10;
        y2-=dy/10;
        if(bold){
            for (int i = -1; i<2; i++){
                g.drawLine(x1+i, y1, x2+i, y2);
                g.drawLine(x1, y1+i, x2, y2+i);
            }
        }else{
            g.drawLine(x1, y1, x2, y2);
        }

//        g.drawLine(x1, y1, x2, y2);

        g.fillPolygon(xpoints, ypoints, 3);
    }
}