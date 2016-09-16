package net.sf.selibs.utils.fsm;

import lombok.Data;



/**
 * Абстрактный класс машины состояния.
 *
 * @author arbocdi
 */
@Data
public abstract class AbstractContext {
    
    protected State state;
    protected State endState;
    protected volatile boolean stopped = false;

    /**
     * Запустить машину, последняя работает пока не остановлена,поток не прерван
     * и не выброшено исключение в processState().
     *
     * @throws Exception
     */
    public void launchFSM() throws Exception {
        while (!stopped) {
            try {
                state.processState(this);
            } catch (Exception ex) {
                if (this.endState != null) {
                    this.setState(this.endState);
                    state.processState(this);
                }
                throw ex;
            }
        }
    }

}
