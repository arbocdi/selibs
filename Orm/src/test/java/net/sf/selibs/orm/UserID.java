
package net.sf.selibs.orm;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import lombok.ToString;

/**
 *
 * @author selibs
 */
@ToString
public class UserID {
    @Column(name="user_id")
    @GeneratedValue
    public int id;
}
