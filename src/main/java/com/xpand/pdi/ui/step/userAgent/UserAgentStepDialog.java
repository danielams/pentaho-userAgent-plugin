package com.xpand.pdi.ui.step.userAgent;

import com.xpand.pdi.step.userAgent.UserAgentStep;
import com.xpand.pdi.step.userAgent.UserAgentStepMeta;
import com.xpand.pdi.step.userAgent.UserAgentStepMetaFunction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.util.*;
import java.util.List;

/**
 * Created by dams on 09-07-2016.
 */
public class UserAgentStepDialog extends BaseStepDialog implements StepDialogInterface {

    private static Class<?> PKG = UserAgentStepMeta.class;

    // this is the object the stores the step's settings
    // the dialog reads the settings from it when opening
    // the dialog writes the settings to it when confirmed
    private UserAgentStepMeta meta;
    private UserAgentStepMeta originalMeta;

    // text field holding the name of the field to add to the row stream
    //private Text wHelloFieldName; // Remove

    private Label wlStepname;
    private Text wStepname;
    private FormData fdlStepname, fdStepname;

    private Label wlFields;
    private TableView wFields;
    private FormData fdlFields, fdFields;

    private Map<String, Integer> inputFields;
    private ColumnInfo[] colinf;

    /**
     * The constructor should simply invoke super() and save the incoming meta
     * object to a local variable, so it can conveniently read and write settings
     * from/to it.
     *
     * @param parent 	the SWT shell to open the dialog in
     * @param in		the meta object holding the step's settings
     * @param transMeta	transformation description
     * @param sname		the step name
     */
    public UserAgentStepDialog(Shell parent, Object in, TransMeta transMeta, String sname){
        super(parent, (BaseStepMeta) in, transMeta, sname);

        meta = (UserAgentStepMeta) in;
        originalMeta = (UserAgentStepMeta) meta.clone();
        inputFields = new HashMap<String, Integer>();
    }

    /**
     * This method is called by Spoon when the user opens the settings dialog of the step.
     * It should open the dialog and return only once the dialog has been closed by the user.
     *
     * If the user confirms the dialog, the meta object (passed in the constructor) must
     * be updated to reflect the new step settings. The changed flag of the meta object must
     * reflect whether the step configuration was changed by the dialog.
     *
     * If the user cancels the dialog, the meta object must not be updated, and its changed flag
     * must remain unaltered.
     *
     * The open() method must return the name of the step after the user has confirmed the dialog,
     * or null if the user cancelled the dialog.
     */
    public String open() {

        // store some convenient SWT variables
        Shell parent = getParent();
        Display display = parent.getDisplay();

        // SWT code for preparing the dialog
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
        props.setLook(shell);
        setShellImage(shell, meta);

        // Save the value of the changed flag on the meta object. If the user cancels
        // the dialog, it will be restored to this saved value.
        // The "changed" variable is inherited from BaseStepDialog
        changed = meta.hasChanged();

        // The ModifyListener used on all controls. It will update the meta object to
        // indicate that changes are being made.
        ModifyListener lsMod = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                meta.setChanged();
            }
        };

        // ------------------------------------------------------- //
        // SWT code for building the actual settings dialog        //
        // ------------------------------------------------------- //
        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "UserAgent.Shell.Title"));

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Stepname line
        wlStepname = new Label(shell, SWT.RIGHT);
        wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
        props.setLook(wlStepname);
        fdlStepname = new FormData();
        fdlStepname.left = new FormAttachment(0, 0);
        fdlStepname.right = new FormAttachment(middle, -margin);
        fdlStepname.top = new FormAttachment(0, margin);
        wlStepname.setLayoutData(fdlStepname);

        wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wStepname.setText(stepname);
        props.setLook(wStepname);
        wStepname.addModifyListener(lsMod);
        fdStepname = new FormData();
        fdStepname.left = new FormAttachment(middle, 0);
        fdStepname.top = new FormAttachment(0, margin);
        fdStepname.right = new FormAttachment(100, 0);
        wStepname.setLayoutData(fdStepname);

        //Fields line
        wlFields = new Label(shell, SWT.NONE);
        wlFields.setText(BaseMessages.getString(PKG, "UserAgent.fields.label"));
        props.setLook( wlFields );
        fdlFields = new FormData();
        fdlFields.left = new FormAttachment( 0, 0 );
        fdlFields.top = new FormAttachment( wStepname, margin );
        wlFields.setLayoutData( fdlFields );

        final int FieldsRows = meta.getUserAgents() != null ? meta.getUserAgents().length : 1;

        colinf =
                new ColumnInfo[] {
                        new ColumnInfo(
                                BaseMessages.getString( PKG, "UserAgent.NewFieldColumn.Column" ),
                                ColumnInfo.COLUMN_TYPE_TEXT, false ),
                        new ColumnInfo(
                                BaseMessages.getString( PKG, "UserAgent.FieldColumn.Column" ),
                                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false ),
                        new ColumnInfo(
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.Column" ),
                                ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] {
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.deviceType" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.manufacturerOS" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.operatingSystem" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.browserManufacturer" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.browserGroup" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.browserName" ),
                                BaseMessages.getString( PKG, "UserAgent.UserAgentType.browserVersion" ) } ), };


        wFields =
                new TableView(
                        transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod, props );

        fdFields = new FormData();
        fdFields.left = new FormAttachment( 0, 0 );
        fdFields.top = new FormAttachment( wlFields, margin );
        fdFields.right = new FormAttachment( 100, 0 );
        fdFields.bottom = new FormAttachment( 100, -50 );
        wFields.setLayoutData( fdFields );

        //
        // Search the fields in the background
        //
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StepMeta stepMeta = transMeta.findStep( stepname );
                if ( stepMeta != null ) {
                    try {
                        RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );

                        // Remember these fields...
                        for ( int i = 0; i < row.size(); i++ ) {
                            inputFields.put( row.getValueMeta( i ).getName(), Integer.valueOf( i ) );
                        }

                        setComboBoxes();
                    } catch ( KettleException e ) {
                        logError( BaseMessages.getString( PKG, "UserAgent.Log.UnableToFindInput" ) );
                    }
                }
            }
        };
        new Thread( runnable ).start();

        wFields.addModifyListener( new ModifyListener() {
            @Override
            public void modifyText( ModifyEvent arg0 ) {
                // Now set the combo's
                shell.getDisplay().asyncExec( new Runnable() {
                    @Override
                    public void run() {
                        setComboBoxes();
                    }

                } );

            }
        } );


        // OK and cancel buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

        // Add listeners for cancel and OK
        lsCancel = new Listener() {
            public void handleEvent(Event e) {cancel();}
        };
        lsOK = new Listener() {
            public void handleEvent(Event e) {ok();}
        };

        wCancel.addListener(SWT.Selection, lsCancel);
        wOK.addListener(SWT.Selection, lsOK);

        // default listener (for hitting "enter")
        lsDef = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {ok();}
        };
        wStepname.addSelectionListener(lsDef);

        // Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
        shell.addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {cancel();}
        });

        // Set/Restore the dialog size based on last position on screen
        // The setSize() method is inherited from BaseStepDialog
        setSize();

        // populate the dialog with the values from the meta object
        getData();

        // restore the changed flag to original value, as the modify listeners fire during dialog population
        meta.setChanged(changed);

        // open dialog and enter event loop
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        // at this point the dialog has closed, so either ok() or cancel() have been executed
        // The "stepname" variable is inherited from BaseStepDialog
        return stepname;
    }

    protected void setComboBoxes() {
        // Something was changed in the row.
        //
        final Map<String, Integer> fields = new HashMap<String, Integer>();

        // Add the currentMeta fields...
        fields.putAll( inputFields );

        shell.getDisplay().syncExec( new Runnable() {
            @Override
            public void run() {
                // Add the newly create fields.
                //
                int nrNonEmptyFields = wFields.nrNonEmpty();
                for ( int i = 0; i < nrNonEmptyFields; i++ ) {
                    TableItem item = wFields.getNonEmpty( i );
                    fields.put( item.getText( 1 ), Integer.valueOf( 1000000 + i ) ); // The number is just to debug the origin of
                    // the fieldname
                }
            }
        } );

        Set<String> keySet = fields.keySet();
        List<String> entries = new ArrayList<String>( keySet );

        String[] fieldNames = entries.toArray( new String[entries.size()] );

        Const.sortStrings( fieldNames );
        colinf[1].setComboValues( fieldNames );
    }

    /**
     * This helper method puts the step configuration stored in the meta object
     * and puts it into the dialog controls.
     */
    private void getData() {
        if ( meta.getUserAgents() != null ) {
            for ( int i = 0; i < meta.getUserAgents().length; i++ ) {
                UserAgentStepMetaFunction fn = meta.getUserAgents()[i];
                TableItem item = wFields.table.getItem( i );
                item.setText( 1, Const.NVL( fn.getFieldName(), "" ) );
                item.setText( 2, Const.NVL( fn.getField(), "" ) );
                item.setText( 3, Const.NVL( fn.getTypeField(), "" ) );
            }
        }

        wFields.setRowNums();
        wFields.optWidth( true );

        wStepname.selectAll();
        wStepname.setFocus();
    }

    /**
     * Called when the user cancels the dialog.
     */
    private void cancel() {
        // The "stepname" variable will be the return value for the open() method.
        // Setting to null to indicate that dialog was cancelled.
        stepname = null;
        // Restoring original "changed" flag on the met aobject
        meta.setChanged(changed);
        // close the SWT dialog window
        dispose();
    }

    /**
     * Called when the user confirms the dialog
     */
    private void ok() {

        // The "stepname" variable will be the return value for the open() method.
        // Setting to step name from the dialog control
        stepname = wStepname.getText();
        // Setting the  settings to the meta object

        int nrNonEmptyFields = wFields.nrNonEmpty();
        meta.allocate( nrNonEmptyFields );

        for ( int i = 0; i < nrNonEmptyFields; i++ ) {
            TableItem item = wFields.getNonEmpty( i );

            String fieldName = item.getText( 1 );
            String field = item.getText( 2 );
            String typeField = item.getText( 3 );

            //CHECKSTYLE:Indentation:OFF
            meta.getUserAgents()[i] = new UserAgentStepMetaFunction(fieldName, field,typeField);
        }

        if ( !originalMeta.equals( meta ) ) {
            meta.setChanged();
            changed = meta.hasChanged();
        }

        // close the SWT dialog window
        dispose();
    }
}
