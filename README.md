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

JDK 1.7 or later  
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

#### IMacroRecorder - Interface of the single instance that records change macros

The single instance can be obtained from the invocation as `MacroRecorder.getInstance()`. 

    public interface IMacroRecorder {
    
        /**
         * Returns the compressor that compresses change macros.
         * @return the macro compressor
         */
        public IMacroCompressor getMacroCompressor();
        
        /**
         * Sets a compressor that compresses change macros.
         * @param compressor the compressor
         */
        public void setMacroCompressor(IMacroCompressor compressor);
        
        /**
         * Starts the recording of change macros.
         */
        public void start();
        
        /**
         * Stops the recording of change macros.
         */
        public void stop();
        
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
    }

#### IMacroCompressor - Interface of an instance that compresses document change macros

The default delimiters are `'\n'`, `'\r'`, `','`, `'.'`, `';'`, `'('`, `')'`, `'{'`, `'}'`. ChangeMacroRecorder delimits continuous typing at the point where it detects one of these characters. in other words, typing the text of "ab(c)" are divided into four document change macros: "ab", "(", "c", and ")". The characters "a" and "b" are combined since a delimiter does not exist between "a" and "b". The user program freely the delimiter characters by using method `setDelimiter(char[])`. If the user program wants to replace the default delimiter-based algorithm with a different one, the program can implement `canCombine(DocumentMacro)` and `combine(DocumentMacro, DocumentMacro)`.

    import org.jtool.macrorecorder.macro.DocumentMacro;
    
    /**
      * An interface for compressor of change macros.
      */
    public interface IMacroCompressor {
    
        /**
         * Sets characters that delimit recorded document change macros.
         * @param chars characters representing delimiters
         */
        public void setDelimiter(char[] chars);
        
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
    }

#### IMacroListener - Interface of an instance that receives a change macro event

The user program must implement the four abstract methods. 
* `initialize()` doing the initialization, which will be invoked immediately before starting the macro recording,
* `terminate()` doing the termination, which will invoked immediately after starting the macro recording,
* `macroAdded(MacroEvent)` receiving events that contain amended (after the combination or cancellation) change macros, and
@ `rawMacroAdded(MacroEvent)`, receiving events that contain all non-amended (before the combination or cancellation) change macros.

    public interface IMacroListener {
        
        /**
         * Invoked to initialize this handler immediately before starting the macro recording.
         */
        public void initialize();
        
        /**
         * Invoked to terminate this handler immediately after stopping the macro recording.
         */
        public void terminate();
        
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

For example, if you will create a handler class `SampleMacroPrintHandler` that displays all of the amended code changes on the console, the class contains the following code:

    import org.jtool.macrorecorder.macro.Macro;
    import org.jtool.macrorecorder.recorder.MacroRecorder;
    import org.jtool.macrorecorder.recorder.IMacroListener;
    import org.jtool.macrorecorder.recorder.IMacroRecorder;
    import org.jtool.macrorecorder.recorder.IMacroCompressor;
    import org.jtool.macrorecorder.recorder.MacroEvent;
    import org.jtool.macrorecorder.recorder.MacroConsole;
    
    public class SampleMacroPrintHandler implements IMacroListener {
        
        public SampleMacroPrintHandler() {
        }
        
        @Override
        public void initialize() {
            IMacroRecorder recorder = MacroRecorder.getInstance();
            IMacroCompressor compressor = recorder.getMacroCompressor();
            compressor.setDelimiter(new char[] { '\n' });
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

Note that Eclipse does not run your plug-in until it is needed.If your plug-in records change macros form the beginning of the Eclipse activation, you should register the handler class in the extension point in the
`plugin.xml` as shown below. 

    <extension
             point="org.jtool.macrorecorder.handlers">
          <handler
                class="org.jtool.macrorecorder.sample.SampleMacroPrintHandler"
                commandId="org.eclipse.macrorecorder.handler.SampleMacroPrintHandler">
          </handler>
    </extension>

Your code can directly start and stop recording of change macros by directly invoking `start()` and `stop()` of the instance of `IMacroRecorder`. In this case, it is responsible for registering and unregistering a handler that receives change macros.

    private void start() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.addMacroListener(this);
        recorder.start();
    }
    
    private void stop() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.stop();
        recorder.removeMacroListener(this);
    }

For more detail of API usage, please see [samples](<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.sample>)

## Author

[Katsuhisa Maruyama](http://www.fse.cs.ritsumei.ac.jp/~maru/index.html)
