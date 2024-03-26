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
    REMOVE_CONDITIONALS_EQUAL_IF("Remove Equal Conditionals If", "Remove equal conditions and replace with true, execute if part"),
    REMOVE_CONDITIONALS_EQUAL_ELSE("Remove Equal Conditionals Else", "Remove equal conditions and replace with false, execute else part"),
    REMOVE_CONDITIONALS_ORDER_IF("Remove Order Checks If", "Remove order conditions and replace with true, execute if part"),
    REMOVE_CONDITIONALS_ORDER_ELSE("Remove Order Checks Else", "Remove order conditions and replace with false, execute else part"),
    REMOVE_INCREMENTS("Remove Increments", "Removes local variable increments"),
    EXPERIMENTAL_ARGUMENT_PROPAGATION("Experimentation Argument Propagation", "Replaces method call with one of its parameters of matching type"),
    EXPERIMENTAL_BIG_INTEGER("Experimental Big Integer", "Swaps big integer methods"),
    EXPERIMENTAL_BIG_DECIMAL("Experimental Big Integer", "Swaps big decimal methods"),
    EXPERIMENTAL_NAKED_RECEIVER("Experimental Naked Receiver", "Replaces method call with a naked receiver"),
    EXPERIMENTAL_MEMBER_VARIABLE("Experimental Member Variable", "Removes assignments to member variables. Can even remove assignments to final members. The members will be initialized with their Java Default Value"),
    EXPERIMENTAL_SWITCH("Experimental Switch", "Finds the first label within a switch statement that differs from the default label. Mutates the switch statement by replacing the default label (wherever it is used) with this label. All the other labels are replaced by the default one"),
    REMOVE_SWITCH("Remove Switch","Finds the first switch label that differs from the default label, then replaces the default label with this label, and all other labels are replaced with the default one");

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
