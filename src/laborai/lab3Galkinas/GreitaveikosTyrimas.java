package laborai.lab3Galkinas;

import laborai.demo.Timekeeper;
import laborai.studijosktu.BstSetKTUx2;
import laborai.studijosktu.AvlSetKTUx;
import laborai.studijosktu.SortedSetADTx;
import laborai.studijosktu.BstSetKTUx;
import laborai.gui.MyException;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

public class GreitaveikosTyrimas {

    public static final String FINISH_COMMAND = "finish";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("laborai.gui.messages");

    private static final String[] TYRIMU_VARDAI = {"addBstRec", "addBstIte", "addAvlRec", "removeBst"};
    private static final int[] TIRIAMI_KIEKIAI = {10000, 20000, 40000, 80000};

    private final BlockingQueue resultsLogger = new SynchronousQueue();
    private final Semaphore semaphore = new Semaphore(-1);
    private final Timekeeper tk;
    private final String[] errors;

    private final SortedSetADTx<VaizdoKortos> aSeries = new BstSetKTUx(new VaizdoKortos(), VaizdoKortos.pagalKaina);
    private final SortedSetADTx<VaizdoKortos> aSeries2 = new BstSetKTUx2(new VaizdoKortos());
    private final SortedSetADTx<VaizdoKortos> aSeries3 = new AvlSetKTUx(new VaizdoKortos());

    public GreitaveikosTyrimas() {
        semaphore.release();
        tk = new Timekeeper(TIRIAMI_KIEKIAI, resultsLogger, semaphore);
        errors = new String[]{
                MESSAGES.getString("error1"),
                MESSAGES.getString("error2"),
                MESSAGES.getString("error3"),
                MESSAGES.getString("error4")
        };
    }

    public void pradetiTyrima() {
        try {
            SisteminisTyrimas();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void SisteminisTyrimas() throws InterruptedException {
        try {
            for (int k : TIRIAMI_KIEKIAI) {
                VaizdoKortos[] vaizdoMas = VaizduskiuGamyba.generuotiIrIsmaisyti(k, 1.0);
                aSeries.clear();
                aSeries2.clear();
                aSeries3.clear();
                tk.startAfterPause();
                tk.start();
                for (VaizdoKortos v : vaizdoMas) {
                    aSeries.add(v);
                }
                tk.finish(TYRIMU_VARDAI[0]);
                for (VaizdoKortos v : vaizdoMas) {
                    aSeries2.add(v);
                }
                tk.finish(TYRIMU_VARDAI[1]);
                for (VaizdoKortos v : vaizdoMas) {
                    aSeries3.add(v);
                }
                tk.finish(TYRIMU_VARDAI[2]);
                for (VaizdoKortos v : vaizdoMas) {
                    aSeries.remove(v);
                }
                tk.finish(TYRIMU_VARDAI[3]);
                tk.seriesFinish();
            }
            tk.logResult(FINISH_COMMAND);
        } catch (MyException e) {
            if (e.getCode() >= 0 && e.getCode() <= 3) {
                tk.logResult(errors[e.getCode()] + ": " + e.getMessage());
            } else if (e.getCode() == 4) {
                tk.logResult(MESSAGES.getString("msg3"));
            } else {
                tk.logResult(e.getMessage());
            }
        }
    }

    public BlockingQueue<String> getResultsLogger() {
        return resultsLogger;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
