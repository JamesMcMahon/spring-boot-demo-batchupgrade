package sh.jfm.springbootdemos.batchupgradeexample;

import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User user) {
        user.setFirstName(user.getFirstName().toUpperCase());
        user.setLastName(user.getLastName().toUpperCase());
        return user;
    }
}
