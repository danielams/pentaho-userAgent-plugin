package com.xpand.pdi.step.userAgent;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * Created by dams on 09-07-2016.
 */
public class UserAgentStepData extends BaseStepData implements StepDataInterface {

    public RowMetaInterface outputRowMeta;
     private UserAgentStep.FieldIndexes[] fieldIndexes;

    private int[] tempIndexes;

    public UserAgentStepData(){
        super();
    }

    public void setOutputRowMeta(RowMetaInterface outputRowMeta) {
        this.outputRowMeta = outputRowMeta;
    }

    public void setTempIndexes(int[] tempIndexes) {
        this.tempIndexes = tempIndexes;
    }

    public void setFieldIndexes(UserAgentStep.FieldIndexes[] fieldIndexes) {
        this.fieldIndexes = fieldIndexes;
    }

    public RowMetaInterface getOutputRowMeta() {
        return outputRowMeta;
    }

    public UserAgentStep.FieldIndexes[] getFieldIndexes() {
        return fieldIndexes;
    }

    public int[] getTempIndexes() {
        return tempIndexes;
    }
}
