package annotations.el;

import java.util.Map;
import java.util.TreeMap;

import annotations.SceneAnnotation;
import annotations.util.coll.VivifyingMap;
import org.jetbrains.annotations.NotNull;

/*>>>
import checkers.nullness.quals.Nullable;
import checkers.javari.quals.ReadOnly;
*/

/**
 * An {@link AElement} that represents a type might have annotations on inner
 * types ("generic/array" annotations in the design document). All such elements
 * extend {@link ATypeElement} so that their annotated inner types can be
 * accessed in a uniform fashion. An {@link AElement} holds the annotations on
 * one inner type; {@link #innerTypes} maps locations to inner types.
 */
public class ATypeElement extends AElement {
    static <K extends /*@ReadOnly*/ Object> VivifyingMap<K, ATypeElement> newVivifyingLHMap_ATE() {
        return new VivifyingMap<K, ATypeElement>(
                new TreeMap<K, ATypeElement>()) {
            @Override
            public  ATypeElement createValueFor(K k) {
                return new ATypeElement(k);
            }

            @Override
            public boolean subPrune(ATypeElement v) {
                return v.prune();
            }
        };
    }

    @NotNull
    /**
     * The annotated inner types; map key is the inner type location.
     */
    public final VivifyingMap<InnerTypeLocation, ATypeElement> innerTypes =
        ATypeElement.<InnerTypeLocation>newVivifyingLHMap_ATE();

    // general information about the element being annotated
    public Object description;

    ATypeElement(Object description) {
        super(description);
        this.description = description;
    }

    void checkRep() {
        assert thisType == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(/*>>> @ReadOnly ATypeElement this, */ /*@ReadOnly*/ AElement o) {
        if (!(o instanceof ATypeElement))
            return false;
        /*@ReadOnly*/ ATypeElement o2 = (/*@ReadOnly*/ ATypeElement) o;
        return o2.equalsTypeElement(this);
    }

    // note:  does not call super.equals, so does not check name
    final boolean equalsTypeElement(/*>>> @ReadOnly ATypeElement this, */ /*@ReadOnly*/ ATypeElement o) {
        return equalsElement(o) && innerTypes.equals(o.innerTypes)
            && (thisType == null ? o.thisType == null : thisType.equals(o.thisType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(/*>>> @ReadOnly ATypeElement this*/) {
        checkRep();
        return tlAnnotationsHere.hashCode() + innerTypes.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean prune() {
        checkRep();
        return super.prune() & innerTypes.prune();
    }

    private static final String lineSep = System.getProperty("line.separator");

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ATypeElement: ");
        sb.append(description);
        sb.append(" : ");
        for (SceneAnnotation a : tlAnnotationsHere) {
            sb.append(a.toString());
            sb.append(" ");
        }
        sb.append("{");
        String linePrefix = "  ";
        for (Map.Entry<InnerTypeLocation, ATypeElement> entry : innerTypes.entrySet()) {
            sb.append(linePrefix);
            sb.append(entry.getKey().toString());
            sb.append(" => ");
            sb.append(entry.getValue().toString());
            sb.append(lineSep);
        }
        sb.append("}");
        return sb.toString();
    }
}
