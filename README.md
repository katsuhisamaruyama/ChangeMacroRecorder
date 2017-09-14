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
* FileMacro - a file action (add, remove, open, close, save, activate, refactor move, rename, git event)  
* GitMacro - git event  
* RefactoringMacro - refactoring action  
* ResourceMacro - action for a resource (file, package, and project)  
* TriggerMacro - a trigger action (refactoring, undo, redo, and cursor change)  
* CompoundMacro - a series of macros  

Recorded change macros include detailed information such as the inserted and deleted text for each edit or command. See
<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder/src/org/jtool/macrorecorder/macro>.

## Requirement

JDK 1.7 or later  
Eclipse 4.6 (Neon) or later  

## License

[Eclipse Public License 1.0 (EPL-1.0)](<https://opensource.org/licenses/eclipse-1.0.php>)

## Install

### Using Eclipse Update Site
Select menu items: "Help" -> "Install New Software..." ->  
Input `http://katsuhisamaruyama.github.io/ChangeMacroRecorder/org.jtool.macrorecorder.site/site.xml` in the text field of "Work with:"  

### Manually Downloading

Download the latest release of the jar file in the directory
<https://github.com/katsuhisamaruyama/ChangeMacroRecorder/tree/master/org.jtool.macrorecorder.site/plugins>
and put it in the 'plugins' directory under the Eclipse installation. Eclipse needs to be  restarted.

## Usage

ChangeMacroRecorder is intended to be embeded into the user (your) program that utilizes (analyzing, visualizing, etc.) recorded fine-grained code changes. It provides three important interfaces that are included in the `package org.jtool.macrorecorder.recorder`.

#### IMacroRecorder - Interface of the single instance that records change macros

The single instance can be obtained from the invocation as `MacroRecorder.getInstance()`. 

```
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
```

#### IMacroCompressor - Interface of an instance that compresses document change macros

The default delimiters are `'\n'`, `'\r'`, `','`, `'.'`, `';'`, `'('`, `')'`, `'{'`, `'}'`. ChangeMacroRecorder delimits continuous typing at the point where it detects one of these characters. in other words, typing the text of "ab(c)" are divided into four document chnange macros: "ab", "(", "c", and ")". The chracters "a" and "b" are combined since a delimiter does not exist between "a" and "b". The user program freely the delimiter characters by using method `setDelimiter(char[])`. If the user program wants to replace the default delimiter-based algorithm with a different one, the program can implement `canCombine(DocumentMacro)` and `combine(DocumentMacro, DocumentMacro)`.

```
import org.jtool.macrorecorder.macro.DocumentMacro;

/**
 * An interface for compressor of change macros.
 * @author Katsuhisa Maruyama
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
```

#### IMacroListener - Interface of an instance that receives a change macro event

The user program must implement two abstract methods: `macroAdded(MacroEvent)`, which receives an event containing processed (after the combination or cancellation) change macros, and `rawMacroAdded(MacroEvent)`, which receives an event containing all non-processed (before the combination or cancellation) change macros.

```
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
```

For example, if you will create a class `PrintChangeMacro` that displays all of the fine-grained code changes on the console, the class contains the following code:

```
import org.jtool.macrorecorder.recorder.MacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroRecorder;
import org.jtool.macrorecorder.recorder.IMacroListener;
import org.jtool.macrorecorder.recorder.IMacroCompressor;
import org.jtool.macrorecorder.recorder.MacroEvent;
import org.jtool.macrorecorder.recorder.MacroConsole;

public class PrintChangeMacro implements IMacroListener {

    public void startListeners() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        IMacroCompressor compressor = recorder.getMacroCompressor();
        compressor.setDelimiter(new char[] { '\n' });
        recorder.addMacroListener(this);
        recorder.start();
    }
    
    public void stopListeners() {
        IMacroRecorder recorder = MacroRecorder.getInstance();
        recorder.removeMacroListener(this);
        recorder.stop();
    }
    
    @Override
    public void macroAdded(MacroEvent evt) {
        Macro macro = evt.getMacro();
        MacroConsole.println(macro.toString());
    }
    
    @Override
    public void rawMacroAdded(MacroEvent evt) {
    }
```

## Author

[Katsuhisa Maruyama](http://www.fse.cs.ritsumei.ac.jp/~maru/index.html)
