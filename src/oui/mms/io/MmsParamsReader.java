package oui.mms.io;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

public class MmsParamsReader {

    private static final Logger logger = Logger.getLogger(MmsParamsReader.class.getName());
    private File file;
    private ParameterSet mps;

    public MmsParamsReader(String fileName) throws IOException {
        this.file = new File (fileName);
    }

    public ParameterSet read() throws IOException {
        mps = new ParameterSet();
        mps.setFile(file);
        FileReader fileReader = new FileReader(file);
        mps.setEncoding(fileReader.getEncoding());
        fileReader.close();

        readFormat();
        readFileHeader();
        readComments();
        readDimensions();
        readParameterHeaders();
        readParameterValues();

        return mps;
    }

    private void readFormat() {
//
// Determine line separator -- this is needed so the format doesn't change
// when the file is written back out
//     PC   is CR + LF  "\r\n"
//     Unix is LF       "\n"
//
// The complicating factor is that a Unix format file can be opened, edited, and
// written back out on PC and the format should not change.
//

//        String line = null;
//        String name = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));
            int foo = in.read();

            while (foo != '\0') {
                if (foo == '\r') {
                    foo = in.read();
                    if (foo == '\n') {
                        mps.setEOL("PC");
                        break;
                    }
                } else if (foo == '\n') {
                    mps.setEOL("unix");
                    break;
                }
                foo = in.read();
            }

        } catch (IOException ex1) {
            logger.log(Level.SEVERE, "Can not determine the format (DOS or Unix) of the parameter file {0}", file.getAbsolutePath());

        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }

    private void readFileHeader() {
        // The parameter file header is the first two lines.
        // first line - short description of file
        // second line - version number.

        BufferedReader in = null;

        try {
//            boolean ready = file.ready();
            in = new BufferedReader(new FileReader(file));
//            boolean ready1 = in.ready();
            resetLineCount();

            String line = in.readLine();
            mps.setDescription(line);

            line = readLine(in);
            mps.setVersion(line);

        } catch (IOException ex1) {
            logger.log(Level.SEVERE, "Can not read the header (first two lines) of the parameter file {0}, line number {1}", new Object[]{file.getAbsolutePath(), lineCount});
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }
    private int numCommentLines = 0;
    private String comments = "";

    private void readComments() {
        // Comment are anything between the header (first two lines) and the
        // dimensions (dimensions start with ** Dimensions **
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));
            resetLineCount();
            String line = readLine(in); // description
            line = readLine(in);  // version

            line = readLine(in);  // first potential comment line

            numCommentLines = 0;
            comments = "";
            while (!line.contentEquals("** Dimensions **")) {
                comments = comments + line;
                numCommentLines++;
                line = readLine(in);  // next potential comment line
            }

        } catch (IOException ex1) {
            logger.log(Level.SEVERE, "Can not read the comments (between header and dimensions) of the parameter file {0}, line number {1}", new Object[]{file.getAbsolutePath(), lineCount});
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }

    private void readDimensions() {
        // Dimensions are defined between the ** Dimensions ** tag and the
        // ** Parameters ** tag
        BufferedReader in = null;
        String line = null;
        try {
            in = new BufferedReader(new FileReader(file));
            resetLineCount();
            line = readLine(in);
// Look for the ** Dimensions ** tag
            while (!line.contentEquals("** Dimensions **")) {
                line = readLine(in);
            }

            line = readLine(in);  // Start of first dimension block
            while (line != null) {
                if (line.equals("** Parameters **")) {
                    break;

                } else if (line.startsWith("<history")) {
//                    mps.addHistory(line);
                    line = readLine(in);

                } else if (line.startsWith("####")) {
                    line = readLine(in);
                    String name = line;
                    int size = -999;
                    try {
                        size = Integer.valueOf(readLine(in)).intValue();
                    } catch (NumberFormatException e) {
                        logger.log(Level.SEVERE, "Problem reading the size of dimension {0} in parameter file {1}\nLine is {2} \nError is {3} line number {4}", new Object[]{name, file.getAbsolutePath(), line, e.getMessage(), lineCount});
                    }

                    Dimension dim = new Dimension(name, size);
                    mps.addDimension(dim);
                    line = readLine(in);

                    int i = 0;
                    while (!(line.startsWith("####") || line.startsWith("** Parameters **"))) {

                        if (line.startsWith("@")) {
                            dim.addItemDesc((i - 1), line, size);
                        } else {
                            dim.addItemName(i, line, size);
                            i++;
                        }
                        line = readLine(in);
                    }
                } else {
                    line = readLine(in);
                }
            }

        } catch (EOFException ex1) {
            logger.log(Level.SEVERE, "End of file reached and can not find the Dimesnions declaration block in parameter file {0}", file.getAbsolutePath());
        } catch (IOException E) {
            logger.log(Level.SEVERE, "Problem reading the Dimesnions declaration block in parameter file {0}\nLine is {1} \nError is {2} line number {3}", new Object[]{file.getAbsolutePath(), line, E.getMessage(), lineCount});
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }

    private void readParameterHeaders() {
        // Parameters are defined by the ** Parameters ** tag
        BufferedReader in = null;
        String line = null;
        String name = null;
        try {
            in = new BufferedReader(new FileReader(file));
            resetLineCount();
            line = readLine(in);

// Look for the ** Parameters ** tag
            while (!line.contentEquals("** Parameters **")) {
                line = readLine(in);
            }

// Look for the first block
            line = readLine(in);
            if (!line.contentEquals("####")) {
                logger.log(Level.SEVERE, "Something between ** Parameters ** tag and first parameter block in parameter file {0}, line number {1}", new Object[]{file.getAbsolutePath(), lineCount});
                return;
            }

            boolean done = false;
            do {
                line = readLine(in);
                String[] split = line.split("  *");
                name = split[0];
                line = readLine(in);
                int num_dim = Integer.parseInt(line);
                String dim1Name = readLine(in);
                Dimension[] dims = new Dimension[num_dim];
                dims[0] = (Dimension) (mps.getDimension(dim1Name));

                if (dims[0] == null) {
                    logger.log(Level.SEVERE, "Invalid dimension {0} for parameter {1} at line {2} in parameter file {3}", new Object[]{dim1Name, name, lineCount, file.getAbsolutePath()});
                }

                int dimBasedSize = dims[0].getSize();

                if (num_dim > 1) {
                    String dim2Name = null;
                    if (num_dim == 2) {
                        dim2Name = readLine(in);
                    }
                    dims[1] = (Dimension) (mps.getDimension(dim2Name));

                    if (dims[1] == null) {
                        logger.log(Level.SEVERE, "Invalid dimension {0} for parameter {1} at line {2} in parameter file {3}", new Object[]{dim2Name, name, lineCount, file.getAbsolutePath()});
                    }
                    
                    dimBasedSize = dimBasedSize * dims[1].getSize();
                }

                line = readLine(in);
                int size = Integer.parseInt(line);

                if (size != dimBasedSize) {
                    logger.log(Level.SEVERE, "The total size of parameter {0} does not match the dimension size at line {1} in parameter file {2}", new Object[]{name, lineCount, file.getAbsolutePath()});
                }

                line = readLine(in);
                int type = Integer.parseInt(line);
                Class type_class = null;
                if (type == 1) {
                    type_class = Integer.class;
                } else if (type == 2) {
                    type_class = Float.class;
                } else if (type == 3) {
                    type_class = Double.class;
                } else if (type == 4) {
                    type_class = String.class;
                } else {
                    logger.log(Level.SEVERE, "Invalid type for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
                }

                mps.addParameter(new Parameter(name, 0, dims, type_class, null));

                line = readLine(in);
                while (line != null && !line.contains("####")) {  // Look for the next parameter block but stop if EOF
                    line = readLine(in);
                }
            } while (line != null);

        } catch (IOException ex1) {
            logger.log(Level.SEVERE, "IOException for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
        } catch (NumberFormatException ex2) {
            logger.log(Level.SEVERE, "NumberFormatException for parameter {0} in parameter file {1}, line number {2}, message is {3}", new Object[]{name, file.getAbsolutePath(), lineCount, ex2.getMessage()});
        } catch (NullPointerException ex3) {
            logger.log(Level.SEVERE, "NullPointerException for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }

    private void readParameterValues() {
// Parameters are defined by the ** Parameters ** tag
        BufferedReader in = null;
        String line = null;
        String name = null;
        try {
            in = new BufferedReader(new FileReader(file));
            resetLineCount();
            line = readLine(in);

// Look for the ** Parameters ** tag
            while (!line.contentEquals("** Parameters **")) {
                line = readLine(in);
            }

// Look for the first block
            line = readLine(in);
            if (!line.contentEquals("####")) {
                logger.log(Level.SEVERE, "Something between ** Parameters ** tag and first parameter block in parameter file {0}, line number {1}", new Object[]{file.getAbsolutePath(), lineCount});
                return;
            }

            boolean done = false;
            do {
                line = readLine(in);
                String[] split = line.split("  *");
                name = split[0];

                Parameter parameter = mps.getParameter(name);

                line = readLine(in);  // number of dimensions
                line = readLine(in);  // dimension 1
                if (parameter.getNumDim() == 2) {
                    line = readLine(in); // dimension 2
                }
                line = readLine(in);  // size
                line = readLine(in); // data type

// Read the values in and add them to the parameter object
                int[] intVals = null;
                double[] doubleVals = null;
                float[] floatVals = null;
                String[] stringVals = null;

                if (parameter.getType() == Integer.class) {
                    intVals = new int[parameter.getSize()];

                    int i = 0;
                    while (i < parameter.getSize()) {
                        line = readLine(in);
                        String[] split1 = line.split("  *");
                        for (int j = 0; j < split1.length; j++) {
                            intVals[i++] = Integer.parseInt(split1[j]);
                        }
                    }
                    parameter.setVals(intVals);

                } else if (parameter.getType() == Float.class) {
                    floatVals = new float[parameter.getSize()];

                    int i = 0;
                    while (i < parameter.getSize()) {
                        line = readLine(in);
                        String[] split1 = line.split("  *");
                        for (int j = 0; j < split1.length; j++) {
                            floatVals[i++] = Float.parseFloat(split1[j]);
                        }
                    }
                    parameter.setVals(floatVals);
                
                } else if (parameter.getType() == Double.class) {
                    doubleVals = new double[parameter.getSize()];

                    int i = 0;
                    while (i < parameter.getSize()) {
                        line = readLine(in);
                        String[] split1 = line.split("  *");
                        for (int j = 0; j < split1.length; j++) {
                            doubleVals[i++] = Double.parseDouble(split1[j]);
                        }
                    }
                    parameter.setVals(doubleVals);

                } else if (parameter.getType() == String.class) {
                    stringVals = new String[parameter.getSize()];

                    for (int i = 0; i < parameter.getSize(); i++) {
                        line = readLine(in);
                        String[] split1 = line.split("  *");
                        for (int j = 0; j < split1.length; j++) {
                            stringVals[i] = split1[j];
                        }
                    }
                    parameter.setVals(stringVals);

                } else {
                    logger.log(Level.SEVERE, "Invalid type for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
                }
                
                line = readLine(in);
                while (line != null && !line.contains("####")) {  // Look for the next parameter block but stop if EOF
                    line = readLine(in);
                }
            } while (line != null);

        } catch (IOException ex1) {
            logger.log(Level.SEVERE, "IOException for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
        } catch (NumberFormatException ex2) {
            logger.log(Level.SEVERE, "NumberFormatException for parameter {0} in parameter file {1}, line number {2}, message is {3}", new Object[]{name, file.getAbsolutePath(), lineCount, ex2.getMessage()});
        } catch (NullPointerException ex3) {
            logger.log(Level.SEVERE, "NullPointerException for parameter {0} in parameter file {1}, line number {2}", new Object[]{name, file.getAbsolutePath(), lineCount});
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException E) {
                logger.log(Level.SEVERE, "Problem closing parameter file {0}", file.getAbsolutePath());
            }
        }
    }

    private int lineCount = 0;
    private String readLine(BufferedReader in) throws IOException {
        String line = in.readLine();
        lineCount++;
        return line;
    }

    private void resetLineCount () {
        lineCount = 0;
    }

    public static void main(String arg[]) {
        try {
            MmsParamsReader mp = new MmsParamsReader("C:/markstro/data/acf_firstcut.params");
            ParameterSet ps = mp.read();

//            System.out.println("Dimensions = " + ps.getDims());
//            System.out.println("Parameters = " + ps.getParams());

        } catch (java.io.FileNotFoundException e) {
            System.out.println(arg[0] + " not found");
        } catch (IOException e) {
            System.out.println(arg[0] + " io exception");
        }
    }
    }
