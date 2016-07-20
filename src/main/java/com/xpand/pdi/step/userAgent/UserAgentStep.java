package com.xpand.pdi.step.userAgent;

import eu.bitwalker.useragentutils.UserAgent;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.core.exception.KettleStepException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by dams on 09-07-2016.
 * <p>
 * This class is the implementation of StepInterface.
 * Classes implementing this interface need to:
 * <p>
 * - initialize the step
 * - execute the row processing logic
 * - dispose of the step
 * <p>
 * Please do not create any local fields in a StepInterface class. Store any
 * information related to the processing logic in the supplied step data interface
 * instead.
 */
public class UserAgentStep extends BaseStep implements StepInterface {

    private static final Class<?> PKG = UserAgentStep.class;

    public class FieldIndexes {
        public int indexName;
        public int indexField;
    }

    /**
     * The constructor should simply pass on its arguments to the parent class.
     *
     * @param s                 step description
     * @param stepDataInterface step data class
     * @param c                 step copy
     * @param t                 transformation description
     * @param dis               transformation executing
     */
    public UserAgentStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
        super(s, stepDataInterface, c, t, dis);
    }

    /**
     * This method is called by PDI during transformation startup.
     * <p>
     * It should initialize required for step execution.
     * <p>
     * The meta and data implementations passed in can safely be cast
     * to the step's respective implementations.
     * <p>
     * It is mandatory that super.init() is called to ensure correct behavior.
     * <p>
     * Typical tasks executed here are establishing the connection to a database,
     * as wall as obtaining resources, like file handles.
     *
     * @param smi step meta interface implementation, containing the step settings
     * @param sdi step data interface implementation, used to store runtime information
     * @return true if initialization completed successfully, false if there was an error preventing the step from working.
     */

    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        UserAgentStepMeta meta = (UserAgentStepMeta) smi;
        UserAgentStepData data = (UserAgentStepData) sdi;

        return super.init(meta, data);
    }

    /**
     * Once the transformation starts executing, the processRow() method is called repeatedly
     * by PDI for as long as it returns true. To indicate that a step has finished processing rows
     * this method must call setOutputDone() and return false;
     * <p>
     * Steps which process incoming rows typically call getRow() to read a single row from the
     * input stream, change or add row content, call putRow() to pass the changed row on
     * and return true. If getRow() returns null, no more rows are expected to come in,
     * and the processRow() implementation calls setOutputDone() and returns false to
     * indicate that it is done too.
     * <p>
     * Steps which generate rows typically construct a new row Object[] using a call to
     * RowDataUtil.allocateRowData(numberOfFields), add row content, and call putRow() to
     * pass the new row on. Above process may happen in a loop to generate multiple rows,
     * at the end of which processRow() would call setOutputDone() and return false;
     *
     * @param smi the step meta interface containing the step settings
     * @param sdi the step data interface that should be used to store
     * @return true to indicate that the function should be called again, false if the step is done
     */
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

        // safely cast the step settings (meta) and runtime info (data) to specific implementations
        UserAgentStepMeta meta = (UserAgentStepMeta) smi;
        UserAgentStepData data = (UserAgentStepData) sdi;

        // get incoming row, getRow() potentially blocks waiting for more rows, returns null if no more rows expected
        Object[] r = getRow();

        // if no more rows are expected, indicate step is finished and processRow() should not be called again
        if (r == null) {
            setOutputDone();
            return false;
        }

        // the "first" flag is inherited from the base step implementation
        // it is used to guard some processing tasks, like figuring out field indexes
        // in the row structure that only need to be done once
        if (first) {
            first = false;
            // clone the input row structure and place it in our data object
            data.setOutputRowMeta(getInputRowMeta().clone());
            // use meta.getFields() to change it, so it reflects the output row structure
            meta.getFields(data.getOutputRowMeta(), getStepname(), null, null, this, repository, metaStore);

            data.setFieldIndexes(new FieldIndexes[meta.getUserAgents().length]);
            List<Integer> tempIndexes = new ArrayList<Integer>();

            for (int i = 0; i < meta.getUserAgents().length; i++) {
                UserAgentStepMetaFunction function = meta.getUserAgents()[i];
                data.getFieldIndexes()[i] = new FieldIndexes();

                if (function.getFieldName() != null || function.getFieldName().length() != 0) {
                    data.getFieldIndexes()[i].indexName = data.getOutputRowMeta().indexOfValue(function.getFieldName());
                    if (data.getFieldIndexes()[i].indexName < 0) {
                        // Nope: throw an exception
                        throw new KettleStepException(BaseMessages.getString(
                                PKG, "UserAgent.Error.UnableFindField", function.getFieldName(), "" + (i + 1)));
                    }
                } else {
                    throw new KettleStepException(BaseMessages.getString(PKG, "UserAgent.Error.NoNameField", ""
                            + (i + 1)));
                }

                if (function.getField() != null || function.getField().length() != 0) {
                    data.getFieldIndexes()[i].indexField = data.getOutputRowMeta().indexOfValue(function.getField());
                    if (data.getFieldIndexes()[i].indexField < 0) {
                        throw new KettleStepException("Unable to find the second argument field '"
                                + function.getFieldName() + " " + (i + 1));
                    }
                }

            }
            // Convert temp indexes to int[]
            data.setTempIndexes(new int[tempIndexes.size()]);
            for (int i = 0; i < data.getTempIndexes().length; i++) {
                data.getTempIndexes()[i] = tempIndexes.get(i);
            }
        }

        // First copy the input data to the new result...
        Object[] calcData = RowDataUtil.resizeArray(r, data.getOutputRowMeta().size());


        for (int i = 0, index = getInputRowMeta().size() + i; i < meta.getUserAgents().length; i++, index++) {
            UserAgentStepMetaFunction fn = meta.getUserAgents()[i];

            if (fn.getFieldName() != null || fn.getFieldName().length() != 0) {
                ValueMetaInterface targetMeta = data.getOutputRowMeta().getValueMeta(index);

                ValueMetaInterface metaField = null;
                Object dataField = null;

                if (data.getFieldIndexes()[i].indexField >= 0) {
                    metaField = data.getOutputRowMeta().getValueMeta(data.getFieldIndexes()[i].indexField);
                    dataField = calcData[data.getFieldIndexes()[i].indexField];
                }


                UserAgent userAgent = UserAgent.parseUserAgentString(metaField.getString(dataField));
                String operationType = fn.getTypeField().toString();

                if (operationType != null || operationType.length() != 0) {
                    if (operationType.toString().equals("Device Type")) {
                        calcData[index] = userAgent.getOperatingSystem().getDeviceType().getName();
                    } else if (operationType.toString().equals("OS Manufacturer")) {
                        calcData[index] = userAgent.getOperatingSystem().getManufacturer().getName();
                    } else if (operationType.toString().equals("Operating System")) {
                        calcData[index] = userAgent.getOperatingSystem().getName();
                    } else if (operationType.toString().equals("Browser Manufacturer")) {
                        calcData[index] = userAgent.getBrowser().getManufacturer().getName();
                    } else if (operationType.toString().equals("Browser Group")) {
                        calcData[index] = userAgent.getBrowser().getGroup().getName();
                    } else if (operationType.toString().equals("Browser Name")) {
                        calcData[index] = userAgent.getBrowser().getName();
                    } else if (operationType.toString().equals("Browser Version")) {
                        calcData[index] = userAgent.getBrowserVersion() == null ? "Unknown" : userAgent.getBrowserVersion().getVersion();
                    }

                } else {
                    throw new KettleStepException("Select the Parse Field");
                }
            }
        }


        Object[] outputRow = RowDataUtil.removeItems(calcData, data.getTempIndexes());

        // put the row to the output row stream
        putRow(data.outputRowMeta, outputRow);

        // log progress if it is time to to so
        if (checkFeedback(getLinesRead())) {
            logBasic(BaseMessages.getString(PKG, "Linenr", getLinesRead())); // Some basic logging
        }

        // indicate that processRow() should be called again
        return true;
    }


    /**
     * This method is called by PDI once the step is done processing.
     * <p>
     * The dispose() method is the counterpart to init() and should release any resources
     * acquired for step execution like file handles or database connections.
     * <p>
     * The meta and data implementations passed in can safely be cast
     * to the step's respective implementations.
     * <p>
     * It is mandatory that super.dispose() is called to ensure correct behavior.
     *
     * @param smi step meta interface implementation, containing the step settings
     * @param sdi step data interface implementation, used to store runtime information
     */
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        UserAgentStepMeta meta = (UserAgentStepMeta) smi;
        UserAgentStepData data = (UserAgentStepData) sdi;

        super.dispose(meta, data);
    }

}
