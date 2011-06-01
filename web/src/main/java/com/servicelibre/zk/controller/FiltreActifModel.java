package com.servicelibre.zk.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.io.Serializables;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleGroupsModel;
import org.zkoss.zul.event.GroupsDataListener;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

import com.servicelibre.corpus.manager.FiltreMot;

@Deprecated
public class FiltreActifModel extends FiltreMot implements ListModel, GroupsModel, Selectable, java.io.Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5167237759745545555L;
	private transient List<ListDataListener> _listeners = new LinkedList<ListDataListener>();
	private Set<Object> _selection = new HashSet<Object>();
SimpleGroupsModel g;
GroupsModelArray gg;
	/**
	 * Fires a {@link ListDataEvent} for all registered listener (thru
	 * {@link #addListDataListener}.
	 * 
	 * <p>
	 * Note: you can invoke this method only in an event listener.
	 */
	protected void fireEvent(int type, int index0, int index1) {
		final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
		for (Iterator<ListDataListener> it = _listeners.iterator(); it.hasNext();)
			(it.next()).onChange(evt);
	}

	// -- ListModel --//
	public void addListDataListener(ListDataListener l) {
		if (l == null)
			throw new NullPointerException();
		_listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		_listeners.remove(l);
	}

	// Selectable
	public Set<Object> getSelection() {
		return Collections.unmodifiableSet(_selection);
	}

	public void addSelection(Object obj) {
		_selection.add(obj);
	}

	public void removeSelection(Object obj) {
		_selection.remove(obj);
	}

	public void clearSelection() {
		_selection.clear();
	}

	protected void removeAllSelection(Collection<?> c) {
		_selection.removeAll(c);
	}

	protected void retainAllSelection(Collection<?> c) {
		_selection.retainAll(c);
	}

	// Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _listeners);
	}

	private synchronized void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		_listeners = new LinkedList<ListDataListener>();
		Serializables.smartRead(s, _listeners);
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getChild(int groupIndex, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount(int groupIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroupfoot(int groupIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasGroupfoot(int groupIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addGroupsDataListener(GroupsDataListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGroupsDataListener(GroupsDataListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClose(int groupIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setClose(int groupIndex, boolean close) {
		// TODO Auto-generated method stub

	}
}