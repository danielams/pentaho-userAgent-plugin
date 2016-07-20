package com.xpand.pdi.step.userAgent;

/**
 * Created by dams on 09-07-2016.
 */

import com.xpand.pdi.ui.step.userAgent.UserAgentStepDialog;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;

/**
 * This class is the implementation of StepMetaInterface.
 * Classes implementing this interface need to:
 * <p>
 * - keep track of the step settings
 * - serialize step settings both to xml and a repository
 * - provide new instances of objects implementing StepDialogInterface, StepInterface and StepDataInterface
 * - report on how the step modifies the meta-data of the row-stream (row structure and field types)
 * - perform a sanity-check on the settings provided by the user
 */

@Step(
        id = "UserAgentParserStep",
        image = "com/xpand/pdi/step/userAgent/ua.svg",
        i18nPackageName = "com.xpand.pdi.step.userAgent",
        name = "UserAgentStep.Name",
        description = "UserAgentStep.TooltipDesc",
        categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform"
)
public class UserAgentStepMeta extends BaseStepMeta implements StepMetaInterface{

    private static Class<?> PKG = UserAgentStepMeta.class;

    private UserAgentStepMetaFunction[] userAgents;

    /**
     * Constructor should call super() to make sure the base class has a chance to initialize properly.
     */
    public UserAgentStepMeta() {
        super();
    }

    /**
     * Called by Spoon to get a new instance of the SWT dialog for the step.
     * A standard implementation passing the arguments to the constructor of the step dialog is recommended.
     *
     * @param shell		an SWT Shell
     * @param meta 		description of the step
     * @param transMeta	description of the the transformation
     * @param name		the name of the step
     * @return 			new instance of a dialog for this step
     */
    public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
        return new UserAgentStepDialog(shell, meta, transMeta, name);
    }

    /**
     * Called by PDI to get a new instance of the step implementation.
     * A standard implementation passing the arguments to the constructor of the step class is recommended.
     *
     * @param stepMeta				description of the step
     * @param stepDataInterface		instance of a step data class
     * @param cnr					copy number
     * @param transMeta				description of the transformation
     * @param disp					runtime implementation of the transformation
     * @return						the new instance of a step implementation
     */
    public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
        return new UserAgentStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
    }

    /**
     * Called by PDI to get a new instance of the step data class.
     */
    public StepDataInterface getStepData() {
        return new UserAgentStepData();
    }

    /**
     * This method is called every time a new step is created and should allocate/set the step configuration
     * to sensible defaults. The values set here will be used by Spoon when a new step is created.
     */
    public void setDefault() {
        userAgents = new UserAgentStepMetaFunction[0];
    }

    /**
     * Getter for the name of the field added by this step
     * @return the name of the field added
     */
    public UserAgentStepMetaFunction[] getUserAgents() {
        return userAgents;
    }

    /**
     * Setter for the name of the field added by this step
     * @param userAgents the name of the field added
     */

    public void setUserAgents(UserAgentStepMetaFunction[] userAgents) {
        this.userAgents = userAgents;
    }

    public  void allocate(int nrUserAgents){
        userAgents = new UserAgentStepMetaFunction[nrUserAgents];
    }

    /**
     * This method is used when a step is duplicated in Spoon. It needs to return a deep copy of this
     * step meta object. Be sure to create proper deep copies if the step configuration is stored in
     * modifiable objects.
     *
     * See org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta.clone() for an example on creating
     * a deep copy.
     *
     * @return a deep copy of this
     */
    public Object clone() {
        UserAgentStepMeta retval = (UserAgentStepMeta) super.clone();
        if ( userAgents != null ) {
            retval.allocate( userAgents.length );
            for ( int i = 0; i < userAgents.length; i++ ) {
                ( retval.getUserAgents() )[i] = (UserAgentStepMetaFunction) userAgents[i].clone();
            }
        } else {
            retval.allocate( 0 );
        }
        return retval;
    }

    /**
     * This method is called by Spoon when a step needs to serialize its configuration to XML. The expected
     * return value is an XML fragment consisting of one or more XML tags.
     *
     * Please use org.pentaho.di.core.xml.XMLHandler to conveniently generate the XML.
     *
     * @return a string containing the XML serialization of this step
     */
    public String getXML() throws KettleValueException {

        StringBuffer retval = new StringBuffer(300);
        if(userAgents != null){
            for (UserAgentStepMetaFunction dDates : userAgents
                    ) {
                retval.append( "       " ).append( dDates.getXML() ).append( Const.CR );
            }

        }
        return retval.toString();
    }

    /**
     * This method is called by PDI when a step needs to load its configuration from XML.
     *
     * Please use org.pentaho.di.core.xml.XMLHandler to conveniently read from the
     * XML node passed in.
     *
     * @param stepnode	the XML node containing the configuration
     * @param databases	the databases available in the transformation
     * @param metaStore the metaStore to optionally read from
     */
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

        try {
            int nrDates = XMLHandler.countNodes(stepnode, UserAgentStepMetaFunction.XML_TAG);
            allocate(nrDates);
            for (int i=0; i < nrDates; i++){
                Node dateNode = XMLHandler.getSubNodeByNr(stepnode, UserAgentStepMetaFunction.XML_TAG, i);
                userAgents[i] = new UserAgentStepMetaFunction(dateNode);
            }

        } catch (Exception e) {
            throw new KettleXMLException("User Agent Parser Plugin plugin unable to read step info from XML node", e);
        }

    }

    /**
     * This method is called by Spoon when a step needs to serialize its configuration to a repository.
     * The repository implementation provides the necessary methods to save the step attributes.
     *
     * @param rep					the repository to save to
     * @param metaStore				the metaStore to optionally write to
     * @param id_transformation		the id to use for the transformation when saving
     * @param id_step				the id to use for the step  when saving
     */
    public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException
    {
        try{
            for ( int i = 0; i < userAgents.length; i++ ) {
                userAgents[i].saveRep( rep, metaStore, id_transformation, id_step, i );
            }
        }
        catch(Exception e){
            throw new KettleException("Unable to save step into repository: "+id_step, e);
        }
    }

    /**
     * This method is called by PDI when a step needs to read its configuration from a repository.
     * The repository implementation provides the necessary methods to read the step attributes.
     *
     * @param rep		the repository to read from
     * @param metaStore	the metaStore to optionally read from
     * @param id_step	the id of the step being read
     * @param databases	the databases available in the transformation
     */
    public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException  {
        try{
            int nrCalcs = rep.countNrStepAttributes( id_step, "field_name" );
            allocate( nrCalcs );
            for ( int i = 0; i < nrCalcs; i++ ) {
                userAgents[i] = new UserAgentStepMetaFunction( rep, id_step, i );
            }
        }
        catch(Exception e){
            throw new KettleException("Unable to load step from repository", e);
        }
    }

    /**
     * This method is called to determine the changes the step is making to the row-stream.
     * To that end a RowMetaInterface object is passed in, containing the row-stream structure as it is when entering
     * the step. This method must apply any changes the step makes to the row stream. Usually a step adds fields to the
     * row-stream.
     *
     * @param inputRowMeta		the row structure coming in to the step
     * @param name 				the name of the step making the changes
     * @param info				row structures of any info steps coming in
     * @param nextStep			the description of a step this step is passing rows to
     * @param space				the variable space for resolving variables
     * @param repository		the repository instance optionally read from
     * @param metaStore			the metaStore to optionally read from
     */
    public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException{

		/*
		 * This implementation appends the outputField to the row-stream
		 */

        // a value meta object contains the meta data for a field
        //ValueMetaInterface v = new ValueMeta(outputField, ValueMeta.TYPE_STRING);

        for ( UserAgentStepMetaFunction fn : userAgents ) {
            //if ( !Const.isEmpty( fn.getFieldName() ) ) { // Give error in OSGI
            // the name of the step that adds this field
            ValueMetaInterface v = getValueMeta( fn, name );
            // modify the row structure and add the field this step generates
            inputRowMeta.addValueMeta( v );
            //}
        }

    }

    private ValueMetaInterface getValueMeta( UserAgentStepMetaFunction fn, String origin ) {
        ValueMetaInterface v;
        // What if the user didn't specify a data type?
        // In that case we look for the default data type
        //default type is String 2
        int defaultResultType = ValueMetaFactory.getIdForValueMeta( "String" );

        try {
            v = ValueMetaFactory.createValueMeta( fn.getFieldName(), defaultResultType );
        } catch ( Exception ex ) {
            return null;
        }
        v.setOrigin( origin );

        return v;
    }

    public RowMetaInterface getAllFields( RowMetaInterface inputRowMeta ) {
        RowMetaInterface rowMeta = inputRowMeta.clone();

        for ( UserAgentStepMetaFunction fn : getUserAgents() ) {
            //if ( !Const.isEmpty( fn.getFieldName() ) ) { // Give error in OSGI
            ValueMetaInterface v = getValueMeta( fn, null );
            rowMeta.addValueMeta( v );
            //}
        }
        return rowMeta;
    }

    /**
     * This method is called when the user selects the "Verify Transformation" option in Spoon.
     * A list of remarks is passed in that this method should add to. Each remark is a comment, warning, error, or ok.
     * The method should perform as many checks as necessary to catch design-time errors.
     *
     * Typical checks include:
     * - verify that all mandatory configuration is given
     * - verify that the step receives any input, unless it's a row generating step
     * - verify that the step does not receive any input if it does not take them into account
     * - verify that the step finds fields it relies on in the row-stream
     *
     *   @param remarks		the list of remarks to append to
     *   @param transmeta	the description of the transformation
     *   @param stepMeta	the description of the step
     *   @param prev		the structure of the incoming row-stream
     *   @param input		names of steps sending input to the step
     *   @param output		names of steps this step is sending output to
     *   @param info		fields coming in from info steps
     *   @param metaStore	metaStore to optionally read from
     */
    public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info, VariableSpace space, Repository repository, IMetaStore metaStore)  {

        CheckResult cr;

        // See if there are input streams leading to this step!
        if (input.length > 0) {
            cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "UserAgent.CheckResult.ReceivingRows.OK"), stepMeta);
            remarks.add(cr);
        } else {
            cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "UserAgent.CheckResult.ReceivingRows.ERROR"), stepMeta);
            remarks.add(cr);
        }

    }

}
