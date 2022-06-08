import java.util.List;
import java.util.ArrayList;


public class Poisson2D implements Runnable{
    private double[][] A;
    private double[] b;
    static double[] x_;

    /**
     * Metodo constructor
     * @param A La matriz A
     * @param b Las incognitas
     */
    public Poisson2D(double[][] A, double[] b){
        this.A = A;
        this.b = b;
    }

    public double[][] getA(){
        return A;
    }

    public double[] getB(){
        return b;
    }

    public void setA(double[][] A){
        this.A = A;
    }

    public void setB(double[] b){
        this.b = b;
    }

    public boolean converge(){
        for(int i = 0; i < A.length; ++i){
            double diagonal = Math.abs(A[i][i]);
            double tmp = 0;

            for(int j = 0; j < A.length; ++j){
                if(i != j) tmp += Math.abs(A[i][j]); 
            }
            if(tmp >= diagonal) return false;
        }
        return true;
    }

    public double[] resuelve(int iteraciones){
        if(!converge()){
            System.err.println("Esta vaina no converge, por lo que no tiene solucion");
        }

        double[] x = inicializa(new double[A.length]); 

        for(int k = 0; k < iteraciones; ++k){
            for(int i = 0; i < A.length; ++i){
                double x_0 = 0;
                for(int j = 0; j < A.length; ++j){
                    if(i != j){
                        x_0 += A[i][j]*x[j];
                    }
                }
                x[i] = (b[i] - x_0)/A[i][i];
            }
        }
        return x;
    }

    public double[] inicializa(double[] zeros){
        for(int i = 0; i < zeros.length; ++i){
            zeros[i] = 0;
        }
        return zeros;
    }

    public double[][] inicializa(double[][] zeros){
        for(int i = 0; i < zeros.length; ++i){
            for(int j = 0; j < zeros[0].length; ++j){
                zeros[i][j] = 0;
            }
        }
        return zeros;
    }

    @Override
    public void run() {
        int id = Integer.parseInt(Thread.currentThread().getName());
        resuelve(100,id);
    }

    public void resuelve(int iteraciones, int fila){
        for(int k = 0; k < iteraciones; k++){
            double x_0 = 0;
            for(int j = 0; j< A.length; ++j){
                if(fila != j){
                    x_0 += A[fila][j]*x_[j];
                }
            }
            x_[fila] = (b[fila]-x_0)/A[fila][fila];
        }
    }

    public void diferenciaCentradaA(){
        int n = 10;
        int h = (5-0)/n;
        //double[][] a = inicializa(new double[n+1][n+1]);

        A[0][0] = 1;
        A[n][n] = 1;

        for(int i=1; i<n; ++i){
            A[i][i-1] = 1;
            A[i][i] = -2;
            A[i][i+1] = 1;
        }

        //double [] bd = inicializa(new double[n+1]);
        for(int i=1; i<b.length; i++){
            b[i] = -9*(h*h);
        }
        b[n] = 50;

        resuelve(100);
    }

    public double[] arange(double inicio, double fin, double espacios){
        double [] temp = new double[(int)((fin-inicio)/espacios)];
        double in = espacios;
        for(int i = 0; i < temp.length; i++){
            temp[i] = in;
            in += espacios;
        }
        return temp;
    }

    public void diferenciaCentradaB(){
        //f(x,y) = cos(2xy)
        double dx = .1;
        double dy = .1;
        double beta = dx/dy;        
        double [] valoresX = arange(0,2*Math.PI,dx);
        double [] valoresY = arange(0,2*Math.PI,dy);

        double [][] malla = inicializa(new double[valoresX.length][valoresY.length]);

        for(int i=0; i< valoresX.length; i++){
            for(int j=0; j< valoresY.length; j++){
                malla[i][j] = func(valoresX[i],valoresY[j]);
            }
        }

        double[][] diferenciaCentrada = new double[valoresX.length][valoresY.length];
        List<Double> diferenciaCent = new ArrayList<Double>();
        for(int i=0;i<malla.length;++i){
            for(int j=0;j<malla[0].length;++j){
                diferenciaCentrada[i][j] = malla[i+1][j] + malla[i-1][j] - 2*(1+ beta*beta)*malla[i][j] + (beta*beta)*(malla[i][j+1] + malla[i][j-1]);
                diferenciaCent.add(malla[i+1][j] + malla[i-1][j] - 2*(1+ beta*beta)*malla[i][j] + (beta*beta)*(malla[i][j+1] + malla[i][j-1]));
            }
        }
    }

    public double func(double x, double y){
        return Math.cos(2*x*y);
    }
    
    public static void main(String[] args) throws InterruptedException{
        double[][] A = {
                        {1,0,0,0,0,0,0,0,0,0},
                        {0,-2,0,0,0,0,0,0,0,0},
                        {0,0,-2,0,0,0,0,0,0,0},
                        {0,0,0,-2,0,0,0,0,0,0},
                        {0,0,0,0,-2,0,0,0,0,0},
                        {0,0,0,0,0,-2,0,0,0,0},
                        {0,0,0,0,0,0,-2,0,0,0},
                        {0,0,0,0,0,0,0,-2,0,0},
                        {0,0,0,0,0,0,0,0,-2,0},
                        {0,0,0,0,0,0,0,0,0,1}
                        };
        double[] b= {1,2,3,4,5,6,7,8,9,10};
        Poisson2D p2 = new Poisson2D(A,b);
        
        x_ = new double[10];
        
        double[] res = p2.resuelve(100);

        for(int i=0; i<res.length; i++){
            System.out.println(res[i]);
        }
        /*List<Thread> threads = new ArrayList<Thread>();

        for(int i=0; i<b.length; i++){

        }
        Thread t1 = new Thread(p2,"0");
        Thread t2 = new Thread(p2,"1");

        t1.start();t2.start();
        t1.join();t2.join();

        for(int i = 0; i <x_.length; ++i){
            System.out.println(x_[i]);
        }*/
    }

}