package base.view;

import base.Main;
import base.p2p.file.FakeP2PFile;
import base.p2p.file.P2PFile;
import base.p2p.tracker.FakeTracker;
import base.p2p.transfer.Transfer;
import base.util.TreeTableRoot;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.Dialogs;

import java.io.File;
import java.time.LocalDate;

/**
 * Ethan Petuchowski 1/7/15
 *
 * This one has the file-list scene on the top half, and multiple
 * options for scenes on the bottom half
 *  (e.g. bandwidth usage history, peer list, etc.)
 */
public class TheWindowCtrl {

    /* this allows the FileChooser to access the main-stage */
    private Main main;
    public void setMain(Main main) { this.main = main; }

    /*************************************************************************
     *  TODO just PUT SAMPLE ITEMS in all the places where items should be.  *
     *  Although actually, this should be done inside the Main() CONSTRUCTOR *
     *************************************************************************/

    // automatically called after the fxml file is loaded
    @FXML private void initialize() {
        initializeLocalFileTable();
        initializeTrkrTreeTable();
        addFakeContent();
    }

    private void initializeLocalFileTable() {
        localFileNameColumn.setCellValueFactory(cellData -> cellData.getValue().filenameProperty());
        // not sure if there's a better way to write this
        localFileSizeColumn.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(cellData.getValue().getFilesizeBytes()+" B"));
        percentCompleteColumn.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(cellData.getValue().getPercentComplete()+"%"));

        // TODO this says "when a selection is made in the fileTable, do whatever I want"
        // at this point, I'm not doing anything
        localFileTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> fileWasSelected(newValue));
    }

    private void initializeTrkrTreeTable() {
        trkrTreeTable.setRoot(treeTableRoot);

        // Defining cell content (using a read-only 'property' created on-the-fly)
        trkrAddrAndNameStrCol.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().getValue().getName()));

        /* TODO this will not format correctly the things for trackers */
        trkrFileSizeCol.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().getValue().getSize()+" B"));

        trkrTreeTable.setShowRoot(true); // show the "'root' tree-item"
    }

    private void addFakeContent() {
        FakeP2PFile local1 = FakeP2PFile.genFakeFile();
        FakeP2PFile local2 = FakeP2PFile.genFakeFile();
        FakeP2PFile local3 = FakeP2PFile.genFakeFile();
        FakeTracker defaultFakeTracker = FakeTracker.getDefaultFakeTracker();
        localFileTable.getItems().addAll(local1, local2, local3);

        // Add the defaultFakeTracker as a child of the Tree's root
        SwarmTreeItem trackerItem = new SwarmTreeItem(new SwarmTreeRenderable(defaultFakeTracker));
        trackerItem.setExpanded(true); // as in "click" the "expand" arrow
        treeTableRoot.getChildren().add(trackerItem);
    }

    private void fileWasSelected(P2PFile p2pFile) { if (p2pFile != null) {} else {} }

    /** UPPER PANES **/

    /* TODO I'm thinking it will look something like this:
     * stackoverflow.com/questions/24290072
     * i.e.
     * ===========================================================
     * ||   Tracker/FileName       ||  Size     || #Sd  || #Lch ||
     * -----------------------------------------------------------
     * || \/ Trackers              ||           ||      ||      ||
     * ||   \/ 123.tra.ker.adr:523 ||           ||      ||      ||
     * ||       file1              ||  size1 B  ||  23  ||  31  ||
     * ||       file2              ||  size2 B  ||   6  ||   2  ||
     * ||   \/ 213.tra.ker.adr:412 ||           ||      ||      ||
     * ||       file3              ||  size1 B  ||  21  ||  11  ||
     * ||       file4              ||  size2 B  ||   7  ||   4  ||
     * ||    ...                   ||  ...      || ...  || ...  ||
     * ===========================================================
     */

    SwarmTreeItem treeTableRoot = new SwarmTreeItem(new SwarmTreeRenderable(new TreeTableRoot()));
    @FXML private TreeTableColumn<SwarmTreeRenderable,String> trkrAddrAndNameStrCol;
    @FXML private TreeTableColumn<SwarmTreeRenderable,String> trkrFileSizeCol;
    @FXML private TreeTableColumn<SwarmTreeRenderable,String> trkrFileNumSeedersCol;
    @FXML private TreeTableColumn<SwarmTreeRenderable,String> trkrFileNumLeechersCol;

    @FXML private TreeTableView<SwarmTreeRenderable> trkrTreeTable;

    @FXML private TableView<P2PFile> localFileTable;
    // type1 == type of TableView, type2 == type of cell content
    @FXML private TableColumn<P2PFile,String> localFileNameColumn;
    @FXML private TableColumn<P2PFile,String> localFileSizeColumn;
    @FXML private TableColumn<P2PFile,String> percentCompleteColumn;

    /** LOWER PANES **/
    /** Transfers (progress bars) **/

    @FXML private TableView<Transfer> transfersForSelectedFile;
    @FXML private TableColumn<Transfer,Integer> transferChunkNumCol;
    @FXML private TableColumn<Transfer,ProgressBar> transferProgressCol;

    /** Bandwidth History (graph) **/
    @FXML private LineChart<LocalDate, Integer> bandwidthHistoryLineChart;

    // TODO in TheWindow this is a "category axis" which doesn't really sound like what I want
    @FXML private Axis<LocalDate> bandwidthChartXAxis;
    @FXML private Axis<Integer> bandwidthChartYAxis;

    /** Pieces Diagram **/
    // haven't figured out exactly what this *is* yet.


    /** MENU BAR ITEMS **/

    @FXML private void addFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if (file != null) {
            // TODO turn into P2PFile and add file to TableView<P2PFile> fileList
            // or maybe add it to the ObservableList<P2PFile> localFiles
            // and the table view will "listen" for it
        }
    }
    @FXML private void addTracker() {
        // TODO make a Dialogs for entering in an IPAddress + Port
        // and turn that into a Tracker object and add it to
        // ObservableList<Tracker> knownTrackers
    }
    @FXML private void aboutDialog() {
        // TODO put a real link to the source in the dialog
        Dialogs.create()
               .title("p2p-gui")
               .masthead("About") // this must be that '(i)' icon thing
               .message("By Ethan Petuchowski\n"+
                        "github.com/ethanp/p2p-gui\n"+
                        "December 2015")
               .showInformation();
    }
    @FXML private void quit() { System.exit(0); }

}
