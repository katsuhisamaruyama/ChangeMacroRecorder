# ChangeMacroRecorder

ChangeMacroRecorder is an Eclipse plugin that automatically records fine-grained changes (i.e., editing operatioins, not AST change
operations) by continuously tracking the code edits and commands performed on Eclipse's Java editor.

## Description

ChangeMacroRecorder records the following changes macros:  
* CancelMacro - cancellation of a previous macro  
* CodeCompletionMacro - code completion by quick assist and content assist  
* CommandMacro - execution of command service  
* CopyMacro - copy action  
* DocumentMacro - typing, cut action, paste action, undo action, redo action  
* FileMacro - file action (add, remove, open, close, save, activate, refactor move, rename, git event)  
* GitMacro - git event  
* RefactoringMacro - refactoring action  
* ResourceMacro - action for a resource (file, package, and project)  
* TriggerMacro - trigger action (refactoring, undo, redo, and cursor change)  
* CompoundMacro - series of macros  

Recorded change macros include more detailed information such as the inserted and deleted text for each edit or command. See the source code of the the [macros](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder/src/org/jtool/macrorecorder/macro>).

## Requirement

JDK 1.8 or later  
[Eclipse](https://www.eclipse.org/) 4.6 (Neon) or later 

## License

[Eclipse Public License 1.0 (EPL-1.0)](<https://opensource.org/licenses/eclipse-1.0.php>)

## Install

### Using Eclipse Update Site

Select menu items: "Help" -> "Install New Software..." ->  
Input `http://katsuhisamaruyama.github.io/ChangeMacroRecorder/org.jtool.macrorecorder.site/site.xml` in the text field of "Work with:"  

### Manually Downloading

Download the latest release of the jar file in the [plug-in directory](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.site/plugins>)
and put it in the 'plug-ins' directory under the Eclipse installation. Eclipse needs to be  restarted.

## Usage

ChangeMacroRecorder is intended to be embedded into the user (your) program that utilizes (analyzing, visualizing, etc.) recorded fine-grained code changes. It provides three important interfaces that are included in the `package org.jtool.macrorecorder.recorder`.

### Recording change macros

The single instance can be obtained from the invocation as `MacroRecorder.getInstance()`. 

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
         * Starts the recording of change macros.
         */
        public void start();
        
        /**
         * Stops the recording of change macros.
         */
        public void stop();
        
        /**
         * Tests if this macro recorder is running.
         * @return <code>true</code> if this macro recorder is running, otherwise <code>false</code>
         */
        public boolean isRunning();
        
        /**
         * Sets characters that delimit recorded document change macros.
         * @param chars characters representing delimiters
         */
        public void setDelimiter(char[] chars);
        
        /**
         * Sets a compressor that compresses change macros.
         * @param compressor the compressor
         */
        public void setMacroCompressor(MacroCompressor compressor);
    }

Common code that starts or stops the change macro recording is described below.  

    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.addMacroListener(listener);
    recorder.start();

    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.removeMacroListener(listener);
    recorder.stop();

Note that Eclipse does not automatically run your code. If your code starts recording change macros form the beginning of the Eclipse activation and stops the recording, you should register your code into the extension point (`org.jtool.macrorecorder.handlers`) as a plug-in. In this case, the following code is described in `plugin,xml`.  

    <extension point="org.jtool.macrorecorder.handlers">
        <handler class="SampleMacroHandler" commandId="SampleMMacroHandler">
        </handler>
    </extension>

### Compressing document change macros

Without compression, ChangeMacroTRecorder sends a document macro when each character was recorded. Using compression, successive document macros are combined based on the delimiter-based strategy. The default delimiters are `'\n'`, `'\r'`, `','`, `'.'`, `';'`, `'('`, `')'`, `'{'`, `'}'`. ChangeMacroRecorder delimits successive typing at the point where it detects one of these characters. For example, typing the text of "ab(c)" are divided into four document change macros: "ab", "(", "c", and ")". The characters "a" and "b" are combined since a delimiter does not exist between "a" and "b". Your code freely the delimiter characters by invoking method `setDelimiter(char[])` of interface `IMacroRecorder` as follows.  

    IMacroRecorder recorder = MacroRecorder.getInstance();
    recorder.setDelimiter(new char[] { '\n' });

If you code wants to replace the default delimiter-based strategy with a different one, you will prepare a class implementing `canCombine(DocumentMacro)` and `combine(DocumentMacro, DocumentMacro)` of interface `IMacroCompressor`. This class is registered by invoking `setMacroCompressor(MacroCompressor compressor)` of interface `IMacroRecorder`.  

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
         * @return the combined document change macro, or <code>null</code> if the macro cannot be combined
         */
        public DocumentMacro combine(DocumentMacro last, DocumentMacro next);
    }

### Receiving change macros

You code can receive recorded change macros that are sent from ChangeMacroRecorder to create a class implementing two abstract methods of `IMacroListener`.  

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

If you will register this class as a plug-in of ChangeMacroRecorder, you code also implements three abstract methods of `IMacroHandler'.  

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

### Samples

For example, if you will create a class `SampleMacroPrintHandler` that displays all of the amended code changes on the console, the class contains the following code:  

    import org.jtool.macrorecorder.macro.Macro;
    import org.jtool.macrorecorder.recorder.IMacroRecorder;
    import org.jtool.macrorecorder.recorder.MacroRecorder;
    import org.jtool.macrorecorder.recorder.IMacroHandler;
    import org.jtool.macrorecorder.recorder.MacroEvent;
    import org.jtool.macrorecorder.recorder.MacroConsole;
    
    /**
     * SampleMacroPrintCommand sample handler that prints change macros.
     *
     * This is intended to be specified in the extension point of org.jtool.macrorecorder.handlers.
     * 
     * <extension point="org.jtool.macrorecorder.handlers">
     *     <handler class="org.jtool.macrorecorder.sample.SampleMacroPrintHandler"
     *              commandId="org.eclipse.macrorecorder.handler.SampleMacroPrintHandler">
     *     </handler>
     * </extension>
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
            recorder.setDelimiter(new char[] { '\n' });
        }
        
        @Override
        public void terminate() {
        }
        
        @Override
        public void macroAdded(MacroEvent evt) {
            Macro macro = evt.getMacro();
            MacroConsole.println(macro.getDescription());
        }
        
        @Override
        public void rawMacroAdded(MacroEvent evt) {
        }
    }

The following code directly switches starting and stopping by the execution of the user's command.

    package org.jtool.macrorecorder.sample;
    
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
     * A sample listener that prints change macros.
     * Starting and stopping is switched to each other.
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
            recorder.start();
        }
        
        private void stop() {
            IMacroRecorder recorder = MacroRecorder.getInstance();
            recorder.removeMacroListener(this);
            recorder.stop();
        }
        
        @Override
        public void macroAdded(MacroEvent evt) {
            Macro macro = evt.getMacro();
            MacroConsole.println("## " + macro.getDescription());
        }
        
        @Override
        public void rawMacroAdded(MacroEvent evt) {
        }
    }

For more detail of API usage, please see [samples](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.sample>)

## Author

[Katsuhisa Maruyama](http://www.fse.cs.ritsumei.ac.jp/~maru/index.html)
