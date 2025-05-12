package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.item.ItemProcessor;

/**
 * Processor class for transforming user names in a batch processing workflow.
 * This processor uppercases both the first name and last name of each User.
 */
public class UserNameProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User user) {
        user.setFirstName(user.getFirstName().toUpperCase());
        user.setLastName(user.getLastName().toUpperCase());
        return user;
    }
}
