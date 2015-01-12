package base.p2p.tracker;

import base.p2p.file.P2PFile;
import base.p2p.peer.Peer;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Ethan Petuchowski 1/10/15
 *
 * I'm not sure whether someone is going to need to inherit
 * this or if one implementation of this guy is enough
 */
public class Swarm {
    public ObservableList<Peer> getLeechers() { return leechers.get(); }
    public ListProperty<Peer> leechersProperty() { return leechers; }
    public void setLeechers(ObservableList<Peer> leechers) { this.leechers.set(leechers); }
    public ObservableList<Peer> getSeeders() { return seeders.get(); }
    public ListProperty<Peer> seedersProperty() { return seeders; }
    public void setSeeders(ObservableList<Peer> seeders) { this.seeders.set(seeders); }
    public P2PFile getP2pFile() { return p2pFile.get(); }
    public ObjectProperty<P2PFile> p2pFileProperty() { return p2pFile; }
    public void setP2pFile(P2PFile p2pFile) { this.p2pFile.set(p2pFile); }

    protected final ListProperty<Peer> leechers;
    protected final ListProperty<Peer> seeders;
    protected final ObjectProperty<P2PFile> p2pFile;

    public Swarm(P2PFile baseP2PFile) {
        p2pFile = new SimpleObjectProperty<>(baseP2PFile);
        seeders = new SimpleListProperty<>();
        leechers = new SimpleListProperty<>();
    }
}
