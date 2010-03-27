/*
 * @(#)CommandLineParser.java    Created on 2010-01-22
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * (Very simple) Command line parser.
 *
 * @author  Elmar Baumann
 */
public final class CommandLineParser {
    private final Set<Option> options = new HashSet<Option>();
    private final String[]    args;
    private final String      optionsDelimiter;
    private final String      optionsValuesDelimiter;

    /**
     * Constructor.
     *
     * @param args
     * @param optionsDelimiter
     * @param optionsValuesDelimiter
     * @throws NullPointerException if a parameter is null
     * @throws IllegalArgumentException on invalid arguments
     */
    public CommandLineParser(String[] args, String optionsDelimiter,
                             String optionsValuesDelimiter) {
        if (args == null) {
            throw new NullPointerException("args == null");
        }

        if (optionsDelimiter == null) {
            throw new NullPointerException("optionsDelimiter == null");
        }

        if (optionsValuesDelimiter == null) {
            throw new NullPointerException("optionsValuesDelimiter == null");
        }

        this.args                   = Arrays.copyOf(args, args.length);
        this.optionsDelimiter       = optionsDelimiter;
        this.optionsValuesDelimiter = optionsValuesDelimiter;
        parseOptions();
    }

    public boolean hasOption(String name) {
        for (Option option : options) {
            if (option.name.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Option getOption(String name) {
        for (Option option : options) {
            if (option.name.equalsIgnoreCase(name)) {
                return option;
            }
        }

        return null;
    }

    private void parseOptions() {
        int    length = args.length;
        Option option = null;

        for (int i = 0; i < length; i++) {
            String  arg      = args[i].trim();
            boolean isOption = isOption(arg);

            if (isOption) {
                option = new Option(ensureOptionName(arg, i));
                delimitValuesFromName(arg, option);
                options.add(option);
            } else {
                for (int j = i + 1; (j < length) &&!isOption; j++) {
                    arg      = args[j].trim();
                    isOption = isOption(arg);

                    if (!isOption) {
                        if (arg.startsWith(optionsValuesDelimiter)) {
                            arg = arg.substring(1);
                        }

                        option.addValue(arg.trim());
                        i = j;
                    }
                }
            }
        }
    }

    private boolean isOption(String arg) {
        return arg.startsWith(optionsDelimiter);
    }

    private String ensureOptionName(String arg, int argIndex) {
        String optionName = (arg.length() > 1)
                            ? arg.substring(1)
                            : null;

        if ((optionName == null)
                || optionName.substring(0, 1).equals(optionsValuesDelimiter)) {
            throw new IllegalArgumentException("Invalid option for argument "
                                               + argIndex + ", " + arg);
        }

        StringTokenizer st = new StringTokenizer(optionName,
                                 optionsValuesDelimiter);

        if (st.countTokens() < 1) {
            throw new IllegalArgumentException("Invalid option for argument "
                                               + argIndex + ", " + arg);
        }

        return st.nextToken().trim();
    }

    private void delimitValuesFromName(String arg, Option option) {
        StringTokenizer st = new StringTokenizer(arg, optionsValuesDelimiter);

        if (st.countTokens() < 2) {
            return;
        }

        st.nextToken();

        while (st.hasMoreTokens()) {
            option.addValue(st.nextToken().trim());
        }
    }

    public static class Option {
        private final List<String> values = new ArrayList<String>();;
        private final String       name;

        public Option(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<String> getValues() {
            return values;
        }

        public void addValue(String param) {
            values.add(param);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            final Option other = (Option) obj;

            if ((this.name == null)
                ? (other.name != null)
                : !this.name.equals(other.name)) {
                return false;
            }

            if ((this.values != other.values)
                    && ((this.values == null)
                        ||!this.values.equals(other.values))) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;

            hash = 89 * hash + ((this.name != null)
                                ? this.name.hashCode()
                                : 0);
            hash = 89 * hash + ((this.values != null)
                                ? this.values.hashCode()
                                : 0);

            return hash;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
