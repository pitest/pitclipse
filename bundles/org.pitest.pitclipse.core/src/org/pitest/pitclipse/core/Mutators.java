/*******************************************************************************
 * Copyright 2012-2021 Phil Glover and contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.core;

import java.util.ArrayList;
import java.util.List;

import org.pitest.mutationtest.engine.gregor.config.Mutator;

/**
 * Enum which holds information about the mutators of pit.<br>
 * The name of new values <b>must</b> be the exact String, which is used by PIT
 * in the class {@link Mutator}.
 */
public enum Mutators {
    CUSTOM("Custom mutator data", "This is used for the custom settings of mutators and should not be seen by the user."),
    OLD_DEFAULTS("Old defaults", "&Old default Mutators"),
    DEFAULTS("Defaults", "&Default Mutators"),
    STRONGER("Stronger defaults", "&Stronger Mutators"),
    ALL("All", "&All Mutators"),
    CONDITIONALS_BOUNDARY("Conditionals Boundary", "Replaces the relational operators <, <=, >, >=", true),
    INCREMENTS("Increments", "Mutates increments, decrements and assignment increments and decrements of local variables (stack variables). Replaces increments with decrements and vice versa", true),
    INVERT_NEGS("Invert Negatives", "Inverts negation of integer and floating point numbers", true),
    MATH("Math", "Replaces binary arithmetic operations for either integer or floating-point arithmetic with another operation", true),
    NEGATE_CONDITIONALS("Negate Conditionals", "Mutates all conditionals found", true),
    RETURN_VALS("Return Values", "Mutates the return values of method calls. Depending on the return type of the method another mutation is used"),
    VOID_METHOD_CALLS("Void Method Call", "Removes method calls to void methods", true),
    CONSTRUCTOR_CALLS("Constructor Call", "Replaces constructor calls with null values"),
    RETURNS("All Returns", "Group which includes all return mutators"),
    EMPTY_RETURNS("Empty Returns", "Replaces return values with an 'empty' value", true),
    FALSE_RETURNS("False Returns", "Replaces primitive and boxed boolean return values with false", true),
    TRUE_RETURNS("True Returns", "Replaces primitive and boxed boolean return values with true", true),
    INLINE_CONSTS("Inline Constant", "Mutates inline constants. An inline constant is a literal value assigned to a non-final variable"),
    NULL_RETURNS("Null Returns", "Replaces return values with null. Method that can be mutated by the EMPTY_RETURNS mutator or that are directly annotated with NotNull are not mutated", true),
    NON_VOID_METHOD_CALLS("Non Void Method Call", "Removes method calls to non void methods. Their return value is replaced by the Java Default Value for that specific type"),
    PRIMITIVE_RETURNS("Primite Returns", "Replaces int, short, long, char, float and double return values with 0", true),
    REMOVE_CONDITIONALS("Remove Conditionals", "Removes all conditionals statements such that the guarded statements always execute"),
    REMOVE_CONDITIONALS_EQ_IF("Remove Equal Conditionals If", "Remove equal conditions and replace with true, execute if part"),
    REMOVE_CONDITIONALS_EQ_ELSE("Remove Equal Conditionals Else", "Remove equal conditions and replace with false, execute else part"),
    REMOVE_CONDITIONALS_ORD_IF("Remove Order Checks If", "Remove order conditions and replace with true, execute if part"),
    REMOVE_CONDITIONALS_ORD_ELSE("Remove Order Checks Else", "Remove order conditions and replace with false, execute else part"),
    REMOVE_INCREMENTS("Remove Increments", "Removes local variable increments"),
    EXPERIMENTAL_ARGUMENT_PROPAGATION("Experimentation Argument Propagation", "Replaces method call with one of its parameters of matching type"),
    EXPERIMENTAL_BIG_INTEGER("Experimental Big Integer", "Swaps big integer methods"),
    EXPERIMENTAL_NAKED_RECEIVER("Experimental Naked Receiver", "Replaces method call with a naked receiver"),
    EXPERIMENTAL_MEMBER_VARIABLE("Experimental Member Variable", "Removes assignments to member variables. Can even remove assignments to final members. The members will be initialized with their Java Default Value"),
    EXPERIMENTAL_SWITCH("Experimental Switch", "Finds the first label within a switch statement that differs from the default label. Mutates the switch statement by replacing the default label (wherever it is used) with this label. All the other labels are replaced by the default one"),
    ABS("Negation", "Replaces any use of a numeric variable (local variable, field, array cell) with its negation"),
    AOR("Arithmetic Operator Replacement", "Like the Math mutator, replaces binary arithmetic operations for either integer or floating-point arithmetic with another operation"),
    AOR_1("Arithmetic Operator Replacement 1", "+ -> -, - -> +, * -> /, / -> *, % -> *"),
    AOR_2("Arithmetic Operator Replacement 2", "+ -> *, - -> *, * -> %, / -> %, % -> /"),
    AOR_3("Arithmetic Operator Replacement 3", "+ -> /, - -> /, * -> +, / -> +, % -> +"),
    AOR_4("Arithmetic Operator Replacement 4", "+ -> %, - -> %, * -> -, / -> -, % -> -"),
    AOD("Arithmetic Operator Deletion", "Replaces an arithmetic operation with one of its members"),
    AOD1("Arithmetic Operator Deletion 1", "int a = b + c; -> int a = b;"),
    AOD2("Arithmetic Operator Deletion 2", "int a = b + c; -> int a = c;"),
    CRCR("Constant Replacement", "Like the Inline Constant mutator, mutates inline constant"),
    CRCR1("Constant Replacement 1", "Replaces the inline constant c with 1"),
    CRCR2("Constant Replacement 2", "Replaces the inline constant c with 0"),
    CRCR3("Constant Replacement 3", "Replaces the inline constant c with -1"),
    CRCR4("Constant Replacement 4", "Replaces the inline constant c with -c"),
    CRCR5("Constant Replacement 5", "Replaces the inline constant c with c+1"),
    CRCR6("Constant Replacement 6", "Replaces the inline constant c with c-1"),
    OBBN("Bitwise Operator", "Mutates bitwise and (&) and or (|)"),
    OBBN1("Bitwise Operator 1", "a & b; -> a | b;"),
    OBBN2("Bitwise Operator 2", "a & b; -> a;"),
    OBBN3("Bitwise Operator 3", "a & b; -> b;"),
    ROR("Relational Operator Replacement", "Replaces a relational operator with another one"),
    ROR1("Relational Operator Replacement 1", "< -> <=, <= -> <, > -> <, >= -> <, == -> <, != -> <"),
    ROR2("Relational Operator Replacement 2", "< -> >, <= -> >, > -> <=, >= -> <=, == -> <=, != -> <="),
    ROR3("Relational Operator Replacement 3", "< -> >=, <= -> >=, > -> >=, >= -> >, == -> >, != -> >"),
    ROR4("Relational Operator Replacement 4", "< -> ==, <= -> ==, > -> ==, >= -> ==, == -> >=, != -> >="),
    ROR5("Relational Operator Replacement 5", "< -> !=, <= -> !=, > -> !=, >= -> !=, == -> !=, != -> =="),
    // XXX REMOVE_SWITCH("",""), no data on the page of pitest
    UOI1("Unary Operator Insertion 1", "Inserts a unary operator to a variable call from the variable a to a++"),
    UOI2("Unary Operator Insertion 2", "Inserts a unary operator to a variable call from the variable a to a--"),
    UOI3("Unary Operator Insertion 3", "Inserts a unary operator to a variable call from the variable a to ++a"),
    UOI4("Unary Operator Insertion 4", "Inserts a unary operator to a variable call from the variable a to --a"),
    UOI("Unary Operator Insertion", "Inserts a unary operator (increment or decrement) to a variable call. Affects local variables, array variables, fields and parameters");

    /**
     * Descriptor which is used to display the name of this mutator in a table
     */
    private final String descriptor;
    /**
     * Description of this mutator
     */
    private final String description;
    /**
     * true, if this mutator is contained in the DEFAULTS group of PIT
     */
    private final boolean activeByDefault;

    private Mutators(String descriptor, String description) {
        this(descriptor, description, false);
    }

    private Mutators(String descriptor, String description, boolean activeByDefault) {
        this.descriptor = descriptor;
        this.description = description;
        this.activeByDefault = activeByDefault;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActiveByDefault() {
        return activeByDefault;
    }

    public static List<Mutators> getMainGroup() {
        final ArrayList<Mutators> mainGroup = new ArrayList<>();
        mainGroup.add(DEFAULTS);
        mainGroup.add(STRONGER);
        mainGroup.add(ALL);
        mainGroup.add(OLD_DEFAULTS);
        return mainGroup;
    }

    public static List<String> getDefaultMutators() {
        ArrayList<String> defaultMutators = new ArrayList<>();
        for (Mutators m : values()) {
            if (m.isActiveByDefault()) {
                defaultMutators.add(m.name());
            }
        }
        return defaultMutators;
    }
}
