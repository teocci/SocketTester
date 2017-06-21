package com.github.teocci.socket.tester.model;

import com.github.teocci.socket.tester.util.Util;

import java.util.StringTokenizer;
import javax.swing.table.*;
import javax.swing.event.*;
import java.io.IOException;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class PortModel extends AbstractTableModel
{
    private String[][] data;
    private String port = null;
    private String colName[] = {"Port No", "Use", "Description"};

    /**
     * Creates a new instance of PortModel
     */
    public PortModel(String fileName)
    {
        String fileContent;
        try {
            fileContent = Util.readFile(fileName, this);
        } catch (IOException e) {
            fileContent = "80\tWeb\tNothing";
            System.err.println("Error reading : " + fileName + " : " + e);
        }

        StringTokenizer st1 = new StringTokenizer(fileContent, "\n");
        int count = st1.countTokens();
        data = new String[count][3];
        int col;

        for (int row = 0; st1.hasMoreTokens(); row++) {
            StringTokenizer st2 = new StringTokenizer(st1.nextToken(), "\t");
            for (col = 0; st2.hasMoreTokens(); col++) {
                data[row][col] = st2.nextToken();
            }
            while (col < 2) {
                col++;
                data[row][col] = "";
            }
        }
        addTableModelListener(new TML());
    }

    // Prints data when table changes:
    private class TML implements TableModelListener
    {
        public void tableChanged(TableModelEvent e)
        {
            int row = e.getFirstRow();
            System.out.println("Row changed : " + row);
            port = data[row][0];
        }
    }

    public int getColumnCount() { return data[0].length; }

    public int getRowCount() { return data.length; }

    public Object getValueAt(int row, int col)
    {
        return data[row][col];
    }

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    public String getColumnName(int column)
    {
        return colName[column];
    }

    public String getPort()
    {
        return port;
    }

}
