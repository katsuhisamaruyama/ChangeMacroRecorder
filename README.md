# ChangeMacroRecorder

An Eclipse plug-in that automatically records fine-grained textual changes of Java source code

ChangeMacroRecorder is an Eclipse plug-in that automatically records fine-grained textual changes of source code and actions involving those changes while programmers (developers or maintainers) write and modify their source code.

### Change Macros

Recorded textual changes and actions constitute a series of the following eleven change macros.  

* DocumentMacro - textual changes by typing text, cutting text, pasting text, and activating the undo or redo action  
* CancelMacro - cancellation of the textual changes of previous DocumentMacro  
* CopyMacro - actions of copying text  
* CommandMacro - execution of command services  
* CodeCompletionMacro - code completion by content assist  
* RefactoringMacro - refactoring actions  
* GitMacro - Git events related to refs changing and index changing  
* ResourceMacro - property change of resources (files, packages, and projects)  
* FileMacro - basic file operations (adding, removing, opening, closing, saving, and activating for a file), the move or rename refactoring to a file, and git events for a file  
* TriggerMacro - trigger to start, end, or cancel the composite actions and the change of the cursor location  
* CompoundMacro - a collection of change macros, which composes textual changes that are simultaneously made by the same action  

Recorded change macros include more detailed information such as the inserted and deleted text for each edit or command. See the source code of the the [macros](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder/src/org/jtool/macrorecorder/macro>).

### Demostrations

The following four [demonstrations](<http://www.jtool.org/cmr/demos/demo.html>) are available.  

* Demo 0: Creating a plug-in containing a macro handler that receives change macros</li>
* Demo 1: Recording textual changes in programmer's edit and refactoring</li>
* Demo 2: Recording textual changes in refactoring and its undoing</li>
* Demo 3: Recording textual changes in code completion</li>

## Requirement

JDK 1.8 
[Eclipse](https://www.eclipse.org/) 4.6 (Neon) and later  

## License

[Eclipse Public License 1.0 (EPL-1.0)](<https://opensource.org/licenses/eclipse-1.0.php>)

## Install

### Using Eclipse Update Site

Select menu items: "Help" -> "Install New Software..." ->  
Input `http://katsuhisamaruyama.github.io/ChangeMacroRecorder/org.jtool.macrorecorder.site/site.xml` in the text field of "Work with:"  

### Manually Downloading

Download the latest release of the jar file in the [plug-in directory](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.site/plugins>)
and put it in the 'plug-ins' directory under the Eclipse. Eclipse needs to be restarted.  

### Checking Installation

Select menu items: "Eclipse" -> "Preferences..." -> "ChangemacroRecorder" and toggle the check boxes for displaying raw and treated change macros.  

## Usage

ChangeMacroRecorder is designed to be embedded into various application tools that leverage fine-grained textual changes and adopts the event-listener model in order to notify the tools of the changes. Here is how to utilize four primary interfaces `IMacroRecorder`, `IMacroCompressor`, `IMacroListener`, and `IMacroHandler` that are included in the `package org.jtool.macrorecorder.recorder`.

### IMacroRecorder & IMacroCompressor

This interface provides the functionality of managing the macro recording. Its concrete instance can be obtained by invoking the static method `getInstance()` of the class `MacroRecorder`. Once your code registers or unregisters the receiver instance, it becomes able or unable to receive recorded macros.  

    package org.jtool.macrorecorder.recorder;
    
    /**
     * An interface for recording change macros that were performed on Eclipse.
     */
    public interface IMacroRecorder {
    
        /**
         * Adds a listener that receives a change macro event.
         * @param listener the event listener to be added
         */
        public void addMacroListener(IMacroListener listener);
        
        /**
         * Removes a listener that receives a change macro event.
         * @param listener the event listener to be removed
         */
        public void removeMacroListener(IMacroListener listener);
        
        /**
         * Sets characters that delimit recorded document change macros.
         * @param delimiters string that contains delimiter characters
         */
        public void setDelimiters(String delimiters);
        
        /**
         * Sets a compressor that compresses change macros.
         * @param compressor the compressor
         */
        public void setMacroCompressor(IMacroCompressor compressor);
    }

Common code that starts or stops the change macro recording is described below.  

    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.addMacroListener(listener);  


    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.removeMacroListener(listener);

Without compression, ChangeMacroRecorder sends a document macro when each character was recorded. Using compression, successive document macros are combined based on the delimiter-based strategy. The default delimiters are all characters appearing in string "\n\r ,.;()[]{}". For example, when a programmer inputs the text of "ab;c", this text is separately recorded as DocumentMacro that store four string "a", "b", ";", and "c". In this case, ChangeMacroRecorder concatenates "a" and "b" since there is no delimiter between these two strings. As a result, it creates that store three strings "ab", ";", and "c".  

To customize delimiters, your code passes a string containing all delimiter characters through the invocation to the method `setDelimiters()`. No compression is performed if <code>null</code> is given. On the other hand, the empty string denotes compression of all consecutive texts not chopped by any action.  

    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.setDelimiter("\n");

Moreover, your code can entirely replace a compression strategy of the class `MacroCompressor` with another one. In this case, it registers an instance of a class implementing the interface `IMacroCompressor`, as shown below, by invoking the method `setMacroCompressor()`.  

    package org.jtool.macrorecorder.recorder;
    
    import org.jtool.macrorecorder.macro.DocumentMacro;
    
    /**
     * An interface for document change macros before they are sent to listeners.
     */
    public interface IMacroCompressor {
        
        /**
         * Tests if a document change macros can be combined with its previous document change macro.
         * @param macro the document macro
         * @return <code>true</code> if the macros can be combined, otherwise <code>false</code>
         */
        public boolean canCombine(DocumentMacro macro);
        
        /**
         * Combines successive two document change macros.
         * @param last the former document change macro 
         * @param next the latter document change macro
         * @return the combined document change macro,
         *         or <code>null</code> if the macro cannot be combined
         */
        public DocumentMacro combine(DocumentMacro last, DocumentMacro next);
    }

### IMacroListener &amp; IMacroHandler

To actually receive macros, your user code should prepare a receiver instance of a class that implements the methods `rawMacroAdded()` and `macroAdded()` of `IMacroListener`. Whereas The former receives recorded change macros as-is, the latter receives treated ones. To be precise, ChangeMacroRecorder passes an instance of the class `MacroEvent` that stores either change macro with or without treatment.  

    package org.jtool.macrorecorder.recorder;
    
    /**
     * An interface for receiving change macro events.
     */
    public interface IMacroListener {
        
        /**
         * Receives an event when a new change macro is added.
         * @param evt the macro event
         */
        public void macroAdded(MacroEvent evt);
        
        /**
         * Receives an event when a new raw change macro is added.
         * @param evt the raw macro event
         */
        public void rawMacroAdded(MacroEvent evt);
    }

Besides the basic usage of manually starting and stopping receiving macros, an extension point of the Eclipse plug-in is provided to help you register a receiver instance if you wants to develop a tool that records the whole textual change while Eclipse is running. In this case, your code should define a class for the receiver instance, which implements the methods of `IMacroHandler<` in addition to the methods of `IMacroListener`.  

    package org.jtool.macrorecorder.recorder;
    
    /**
     * An interface for handling received change macro event.
     */
    public interface IMacroHandler extends IMacroListener {
        
        /**
         * Tests if the macro recording is allowed.
         * This handler will be registered if <code>true</code> is returned,
         * otherwise the handler will not be registered.
         */
        public boolean recordingAllowed();
        
        /**
         * Invoked to initialize this handler immediately before starting the macro recording.
         */
        public void initialize();
        
        /**
         * Invoked to terminate this handler immediately after stopping the macro recording.
         */
        public void terminate();
    }

Then, you registers the receiver class into the extension point (`org.jtool.macrorecorder.handlers`) of the plug-in configuration file. For the receiver class `SampleMacroHandler`, the following code is described in `plugin.xml`.  

    <extension point="org.jtool.macrorecorder.handlers">
        <handler class="SampleMacroHandler" commandId="SampleMMacroHandler">
        </handler>
    <extension>

To make your work easier, ChangeMacroRecorder provides the Eclipse's wizard that creates a template of the receiver class and registers. It automatically starts notifying it of recorded macros when Eclipse is activated.  

The method `initialize()` is invoked when Eclipse is activated (not the time when ChangeMacroRecorder starts), and the method `terminate()` is invoked when Eclipse stops. The method <code>recordingAllowed()</code> is responsible for determining whether the change recording is allowed or not. A receiver instance is successfully registered if <code>true</code> is returned, otherwise it is never registered.  

## Samples

### Sample1

The following class switches starting and stopping of the change macro recording by a user's action of pushing a button.  

    package org.jtool.macrorecorder.sample1;
    
    import org.jtool.macrorecorder.macro.Macro;
    import org.jtool.macrorecorder.recorder.IMacroRecorder;
    import org.jtool.macrorecorder.recorder.MacroRecorder;
    import org.jtool.macrorecorder.recorder.MacroConsole;
    import org.jtool.macrorecorder.recorder.MacroEvent;
    import org.jtool.macrorecorder.recorder.IMacroListener;
    import org.eclipse.core.commands.AbstractHandler;
    import org.eclipse.core.commands.ExecutionEvent;
    import org.eclipse.core.commands.ExecutionException;
    
    /**
     * A sample listener that receives execution events and prints change macros.
     */
    public class SampleMacroPrintCommand extends AbstractHandler implements IMacroListener {
        
        private boolean recording = false;
        
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            recording = !recording;
            
            if (recording) {
                start();
            } else {
                stop();
            }
            return null;
        }
        
        private void start() {
            IMacroRecorder recorder = MacroRecorder.getInstance();
            recorder.addMacroListener(this);
        }
        
        private void stop() {
            IMacroRecorder recorder = MacroRecorder.getInstance();
            recorder.removeMacroListener(this);
        }
        
        @Override
        public void macroAdded(MacroEvent evt) {
            Macro macro = evt.getMacro();
            MacroConsole.println("S1 " + macro.getDescription());
        }
        
        @Override
        public void rawMacroAdded(MacroEvent evt) {
        }
    }
    

## Sample2

The following class displays all of the amended code changes on the console.  

    package org.jtool.macrorecorder.sample2;
    
    import org.jtool.macrorecorder.macro.Macro;
    import org.jtool.macrorecorder.recorder.IMacroRecorder;
    import org.jtool.macrorecorder.recorder.MacroRecorder;
    import org.jtool.macrorecorder.recorder.IMacroHandler;
    import org.jtool.macrorecorder.recorder.MacroEvent;
    import org.jtool.macrorecorder.recorder.MacroConsole;
    
    /**
     * A sample handler that prints change macros.
     */
    public class SampleMacroPrintHandler implements IMacroHandler {
        
        public SampleMacroPrintHandler() {
        }
        
        @Override
        public boolean recordingAllowed() {
            return true;
        }
        
        @Override
        public void initialize() {
            IMacroRecorder recorder = MacroRecorder.getInstance();
            recorder.setDelimiters("\n");
        }
        
        @Override
        public void terminate() {
        }
        
        @Override
        public void macroAdded(MacroEvent evt) {
            Macro macro = evt.getMacro();
            MacroConsole.println("S2 " + macro.getDescription());
        }
        
        @Override
        public void rawMacroAdded(MacroEvent evt) {
        }
    }

For more detail of API usage, please see [sample1](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.sample1>), [sample2](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.sample2>), and [sample3](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.sample3>).

## Author

[Katsuhisa Maruyama](http://www.fse.cs.ritsumei.ac.jp/~maru/index.html)
