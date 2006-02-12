/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.apache.axis2.tool.codegen.eclipse.ui;

import org.apache.axis2.tool.codegen.eclipse.plugin.CodegenWizardPlugin;
import org.apache.axis2.tool.codegen.eclipse.util.UIConstants;
import org.apache.axis2.tool.codegen.eclipse.util.WSDLPropertyReader;
import org.apache.axis2.util.URLProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Options Page lets the user change general settings on the code generation. It
 * is used in the CodegenWizardPlugin, CodeGenWizard.
 * 
 */
public class OptionsPage extends AbstractWizardPage implements UIConstants {

	/**
	 * Selection list for target languages
	 */
	private Combo languageSelectionComboBox;

	/**
	 * A radio button to enable/disable code generation for synchronous and
	 * asynchronous calls.
	 */
	private Button syncAndAsyncRadioButton;

	/**
	 * A radio button to choose "synchronous only" code generation
	 */
	private Button syncOnlyRadioButton;

	/**
	 * A radio button to choose "asynchronous only" code generation
	 */
	private Button asyncOnlyRadioButton;

	/**
	 * Label holding the full qualified package name for generated code
	 */
	private Text packageText;

	/**
	 * Checkbox to enable server-side skeleton code generation. If enabled,
	 * generates an empty implementation of the service
	 */
	private Button serverSideCheckBoxButton;

	/**
	 * Checkbox to enable the generation of test case classes for the generated
	 * implementation of the webservice.
	 */
	private Button testCaseCheckBoxButton;

	/**
	 * Checkbox to enable the generation of a default server.xml configuration
	 * file
	 */
	private Button serverXMLCheckBoxButton;

	/**
	 * Checkbox to enable the generate all classes
	 */
	private Button generateAllCheckBoxButton;

	private Combo databindingTypeCombo;

	/**
	 * Text box to have the portname
	 */
	private Combo portNameCombo;

	/**
	 * Text box to have the service name
	 */
	private Combo serviceNameCombo;

	private WSDLPropertyReader reader;

	private List serviceQNameList = null;

	/**
	 * Creates the page and initialize some settings
	 */
	public OptionsPage() {
		super("page2");

	}

	/**
	 * Sets the default values for the Options page
	 * 
	 */
	protected void initializeDefaultSettings() {
		settings.put(PREF_CHECK_GENERATE_SERVERCONFIG, false);
		settings.put(PREF_CHECK_GENERATE_SERVERSIDE, false);
		settings.put(PREF_CHECK_GENERATE_TESTCASE, false);
		settings.put(PREF_LANGUAGE_INDEX, 0);
		settings.put(PREF_PACKAGE_NAME, "org.example.webservice");
		settings.put(PREF_RADIO_ASYNC_ONLY, false);
		settings.put(PREF_RADIO_SYNC_AND_ASYNC, true);
		settings.put(PREF_RADIO_SYNC_ONLY, false);
		settings.put(PREF_COMBO_PORTNAME_INDEX, 0);
		settings.put(PREF_COMBO_SERVICENAME_INDEX, 0);
		settings.put(PREF_DATABINDER_INDEX, 0);
		settings.put(PREF_GEN_ALL, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;

		Label label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin
				.getResourceString("page2.language.caption"));

		languageSelectionComboBox = new Combo(container, SWT.DROP_DOWN
				| SWT.BORDER | SWT.READ_ONLY);
		// fill the combo
		this.fillLanguageCombo();
		languageSelectionComboBox.setLayoutData(gd);
		languageSelectionComboBox.select(settings.getInt(PREF_LANGUAGE_INDEX));
		languageSelectionComboBox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_LANGUAGE_INDEX, languageSelectionComboBox
						.getSelectionIndex());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		syncAndAsyncRadioButton = new Button(container, SWT.RADIO);
		syncAndAsyncRadioButton.setText(CodegenWizardPlugin
				.getResourceString("page2.syncAsync.caption"));
		syncAndAsyncRadioButton.setSelection(settings
				.getBoolean(PREF_RADIO_SYNC_AND_ASYNC));
		syncAndAsyncRadioButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_RADIO_SYNC_AND_ASYNC, syncAndAsyncRadioButton
						.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		syncOnlyRadioButton = new Button(container, SWT.RADIO);
		syncOnlyRadioButton.setText(CodegenWizardPlugin
				.getResourceString("page2.sync.caption"));
		syncOnlyRadioButton.setSelection(settings
				.getBoolean(PREF_RADIO_SYNC_ONLY));
		syncOnlyRadioButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_RADIO_SYNC_ONLY, syncOnlyRadioButton
						.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		asyncOnlyRadioButton = new Button(container, SWT.RADIO);
		asyncOnlyRadioButton
				.setText(org.apache.axis2.tool.codegen.eclipse.plugin.CodegenWizardPlugin
						.getResourceString("page2.async.caption"));
		asyncOnlyRadioButton.setSelection(settings
				.getBoolean(PREF_RADIO_ASYNC_ONLY));
		asyncOnlyRadioButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_RADIO_ASYNC_ONLY, asyncOnlyRadioButton
						.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// service name
		label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin
				.getResourceString("page2.serviceName.caption"));

		serviceNameCombo = new Combo(container, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		serviceNameCombo.setLayoutData(gd);
		// serviceNameCombo.setText(settings.get(PREF_TEXT_SERVICENAME));
		serviceNameCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// update the settings
				settings.put(PREF_COMBO_SERVICENAME_INDEX, serviceNameCombo
						.getSelectionIndex());
				// reload the portName list
				loadPortNames();

			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// port name
		label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin
				.getResourceString("page2.portName.caption"));
		portNameCombo = new Combo(container, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		portNameCombo.setLayoutData(gd);

		portNameCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// do something here
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// package name
		label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin
				.getResourceString("page2.package.caption"));
		packageText = new Text(container, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;

		packageText.setLayoutData(gd);
		String packageName;
		String storedPackageName = settings.get(PREF_PACKAGE_NAME);
		if (storedPackageName.equals("")) {
			packageName = URLProcessor.makePackageName("");
		} else {
			packageName = storedPackageName;
		}
		packageText.setText(packageName); // get this text from the
		// URLProcessor
		packageText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				settings.put(PREF_PACKAGE_NAME, packageText.getText());
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		testCaseCheckBoxButton = new Button(container, SWT.CHECK);
		testCaseCheckBoxButton.setLayoutData(gd);
		testCaseCheckBoxButton
				.setText(org.apache.axis2.tool.codegen.eclipse.plugin.CodegenWizardPlugin
						.getResourceString("page2.testcase.caption"));
		testCaseCheckBoxButton.setSelection(settings
				.getBoolean(PREF_CHECK_GENERATE_TESTCASE));
		testCaseCheckBoxButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_CHECK_GENERATE_TESTCASE,
						testCaseCheckBoxButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// Server side check box
		serverSideCheckBoxButton = new Button(container, SWT.CHECK);
		serverSideCheckBoxButton.setText(CodegenWizardPlugin
				.getResourceString("page2.serverside.caption"));
		serverSideCheckBoxButton.setSelection(settings
				.getBoolean(PREF_CHECK_GENERATE_SERVERSIDE));
		serverSideCheckBoxButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				handleServersideSelection();
				settings.put(PREF_CHECK_GENERATE_SERVERSIDE,
						serverSideCheckBoxButton.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Server side services xml
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		serverXMLCheckBoxButton = new Button(container, SWT.CHECK);
		serverXMLCheckBoxButton.setLayoutData(gd);
		serverXMLCheckBoxButton.setSelection(settings
				.getBoolean(PREF_CHECK_GENERATE_SERVERCONFIG));
		serverXMLCheckBoxButton.setText(CodegenWizardPlugin
				.getResourceString("page2.serviceXML.caption"));
		serverXMLCheckBoxButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_CHECK_GENERATE_SERVERCONFIG,
						serverXMLCheckBoxButton.getEnabled());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// generate all
		generateAllCheckBoxButton = new Button(container, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		generateAllCheckBoxButton.setLayoutData(gd);
		generateAllCheckBoxButton.setSelection(settings
				.getBoolean(PREF_GEN_ALL));
		generateAllCheckBoxButton.setText(CodegenWizardPlugin
				.getResourceString("page2.genAll.caption"));
		generateAllCheckBoxButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_GEN_ALL, generateAllCheckBoxButton
						.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Databinding
		label = new Label(container, SWT.NULL);
		label.setText(CodegenWizardPlugin
				.getResourceString("page2.databindingCheck.caption"));

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		databindingTypeCombo = new Combo(container, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		databindingTypeCombo.setLayoutData(gd);
		fillDatabinderCombo();
		databindingTypeCombo.select(settings.getInt(PREF_DATABINDER_INDEX));
		databindingTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				settings.put(PREF_DATABINDER_INDEX, databindingTypeCombo
						.getSelectionIndex());

			};

			public void widgetDefaultSelected(SelectionEvent e) {
			};
		});

		/*
		 * Check the state of server-side selection, so we can enable/disable
		 * the serverXML checkbox button.
		 */
		handleServersideSelection();
		/*
		 * try populating the combos and other information from the WSDL if this
		 * is restored
		 */
		if (restoredFromPreviousSettings) {
			populateServiceAndPort();

			selectDefaults();
		}

		setControl(container);

		setPageComplete(true);

	}

	private void selectDefaults() {
		serviceNameCombo.select(settings.getInt(PREF_COMBO_SERVICENAME_INDEX));
		// ports need to be renamed in order for correct default selection
		loadPortNames();
		portNameCombo.select(settings.getInt(PREF_COMBO_SERVICENAME_INDEX));
	}

	private void populatePackageName() {
		this.packageText.setText(reader.packageFromTargetNamespace());
	}

	/**
	 * populate the service and the port from the WSDL this needs to be public
	 * since the WSDLselection page may call this
	 */
	public void populateServiceAndPort() {
		if (reader == null)
			reader = new WSDLPropertyReader();
		try {
			String lname = getCodegenWizard().getWSDLname();
			if (!"".equals(lname.trim())) {
				reader.readWSDL(lname);

				// enable the combo's
				setComboBoxEnable(true);

				this.serviceQNameList = reader.getServiceList();
				if (!serviceQNameList.isEmpty()) {
					serviceNameCombo.removeAll();
					for (int i = 0; i < serviceQNameList.size(); i++) {
						// add the local part of the
						serviceNameCombo.add(((QName) serviceQNameList.get(i))
								.getLocalPart());
					}
					;
					// select the first one as the default
					serviceNameCombo.select(0);

					// load the ports
					loadPortNames();

					updateStatus(null);
				} else {
					// service name list being empty means we are switching to
					// the interface mode
					if (serviceNameCombo!=null) serviceNameCombo.removeAll();
					if (portNameCombo!=null) portNameCombo.removeAll();
					// disable the combo's
					setComboBoxEnable(false);
					//this is not an error
					updateStatus(null);
			
				}

				populatePackageName();
			}
		} catch (Exception e) {
			// disable the combo's
			setComboBoxEnable(false);

			updateStatus(CodegenWizardPlugin
					.getResourceString("page2.wsdlNotFound.message"));
		}

	}

	private void loadPortNames() {
		int selectionIndex = serviceNameCombo.getSelectionIndex();
		if (selectionIndex != -1) {
			List ports = reader.getPortNameList((QName) serviceQNameList
					.get(selectionIndex));
			if (!ports.isEmpty()) {
				portNameCombo.removeAll();
				for (int i = 0; i < ports.size(); i++) {
					// add the local part of the
					portNameCombo.add(ports.get(i).toString());
				}
				updateStatus(null);
				portNameCombo.select(0);
			} else {
				updateStatus(CodegenWizardPlugin
						.getResourceString("page2.noports.message"));// TODO
			}
		}
	}

	private void setComboBoxEnable(boolean b) {
		if (serviceNameCombo != null) {
			serviceNameCombo.setEnabled(b);
			portNameCombo.setEnabled(b);
		}
	}

	/**
	 * Fill the combo with proper language names
	 * 
	 */
	private void fillDatabinderCombo() {

		databindingTypeCombo.add(DATA_BINDING_ADB);
		databindingTypeCombo.add(DATA_BINDING_XMLBEANS);
		databindingTypeCombo.add(DATA_BINDING_NONE);

	}

	/**
	 * Fill the combo with proper language names
	 * 
	 */
	private void fillLanguageCombo() {

		languageSelectionComboBox.add(JAVA);
		languageSelectionComboBox.add(C_SHARP);

		languageSelectionComboBox.select(0);
	}

	/**
	 * Validates the status of the server-side checkbox, and enables/disables
	 * the generation checkbox for XML configuration file
	 */
	private void handleServersideSelection() {
		if (this.serverSideCheckBoxButton.getSelection()) {
			this.serverXMLCheckBoxButton.setEnabled(true);
			this.generateAllCheckBoxButton.setEnabled(true);
		} else {
			this.serverXMLCheckBoxButton.setEnabled(false);
			this.generateAllCheckBoxButton.setEnabled(false);
		}
	}

	/**
	 * Get the selected language
	 * 
	 * @return a string containing the name of the target language
	 */
	public String getSelectedLanguage() {
		return languageSelectionComboBox.getItem(languageSelectionComboBox
				.getSelectionIndex());
	}

	/**
	 * the async only status
	 * 
	 * @return true if "Generate asynchronous code only" is checked
	 */
	public boolean isAsyncOnlyOn() {
		return asyncOnlyRadioButton.getSelection();
	}

	/**
	 * the sync only status
	 * 
	 * @return true if "Generate synchronous code only" is checked
	 */
	public boolean isSyncOnlyOn() {
		return syncOnlyRadioButton.getSelection();
	}

	/**
	 * return the package name
	 * 
	 * @return a string containing the package name to use for code generation
	 */
	public String getPackageName() {
		return this.packageText.getText();
	}

	/**
	 * The serverside status
	 * 
	 * @return true if "Generate Server-Side" is checked
	 */
	public boolean isServerside() {
		return this.serverSideCheckBoxButton.getSelection();
	}

	/**
	 * 
	 * @return true if "Generate XML configuration file" is checked
	 */
	public boolean isServerXML() {
		if (this.serverXMLCheckBoxButton.isEnabled())
			return this.serverXMLCheckBoxButton.getSelection();
		else
			return false;
	}

	/**
	 * 
	 * @return true if "Generate test case" is checked
	 */
	public boolean isGenerateTestCase() {
		return this.testCaseCheckBoxButton.getSelection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis2.tool.codegen.eclipse.ui.CodegenPage#getPageType()
	 */
	public int getPageType() {
		return WSDL_2_JAVA_TYPE;
	}

	/**
	 * 
	 * @return null if portname is empty
	 */
	public String getPortName() {
		int selectionIndex = portNameCombo.getSelectionIndex();
		if (selectionIndex != -1) {
			String text = this.portNameCombo.getItem(selectionIndex);

			if (text == null || text.trim().equals("")) {
				return null;
			}
			return text;
		} else {
			return null;
		}
	}

	/**
	 * @return null if the text is empty
	 * 
	 */
	public String getServiceName() {
		int selectionIndex = serviceNameCombo.getSelectionIndex();
		// cater for the scenario where the combo's can be empty
		if (selectionIndex != -1) {
			String text = this.serviceNameCombo.getItem(selectionIndex);

			if (text == null || text.trim().equals("")) {
				return null;
			}
			return text;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getDatabinderName() {
		return this.databindingTypeCombo.getItem(databindingTypeCombo
				.getSelectionIndex());

	}

	public boolean getGenerateAll() {
		return this.generateAllCheckBoxButton.getSelection();
	}
}
