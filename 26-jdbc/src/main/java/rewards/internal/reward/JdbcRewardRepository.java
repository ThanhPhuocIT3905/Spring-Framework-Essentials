package rewards.internal.reward;

import common.datetime.SimpleDate;
import rewards.AccountContribution;
import rewards.Dining;
import rewards.RewardConfirmation;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

/**
 * JDBC implementation of a reward repository that records the result
 * of a reward transaction by inserting a reward confirmation record.
 */

// TODO-08 (Optional) : Inject JdbcTemplate directly to this repository class
// - Refactor the constructor to get the JdbcTemplate injected directly
//   (instead of DataSource getting injected)
// - Refactor RewardsConfig accordingly
// - Refactor JdbcRewardRepositoryTests accordingly
// - Run JdbcRewardRepositoryTests and verity it passes

// TODO-03: Refactor the cumbersome low-level JDBC code in JdbcRewardRepository with JdbcTemplate.
// - Add a field of type JdbcTemplate.
// - Refactor the code in the constructor to instantiate JdbcTemplate
//   object from the given DataSource object.
// - Refactor the confirmReward(...) and nextConfirmationNumber() methods to use
//   the JdbcTemplate object.
//
//   DO NOT delete the old JDBC code, just comment out the try/catch block.
//   You will need to refer to the old JDBC code to write the new code.
//
// - Run JdbcRewardRepositoryTests and verity it passes
//   (If you are using Gradle, make sure to comment out the exclude statement
//    in the test task in the build.gradle.)

public class JdbcRewardRepository implements RewardRepository {

	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	public JdbcRewardRepository(JdbcTemplate jdbcTemplate) {
		this.dataSource = dataSource;
		this.jdbcTemplate = jdbcTemplate;
	}

	public RewardConfirmation confirmReward(AccountContribution contribution, Dining dining) {
		String sql = "insert into T_REWARD (CONFIRMATION_NUMBER, REWARD_AMOUNT, REWARD_DATE, ACCOUNT_NUMBER, DINING_MERCHANT_NUMBER, DINING_DATE, DINING_AMOUNT) values (?, ?, ?, ?, ?, ?, ?)";
		String confirmationNumber = nextConfirmationNumber();

		jdbcTemplate.update(sql, confirmationNumber, contribution.getAmount().asBigDecimal(), new Date(SimpleDate.today().inMilliseconds()),
				contribution.getAccountNumber(), dining.getMerchantNumber(), new Date(dining.getDate().inMilliseconds()), dining.getAmount().asBigDecimal());
				// Sử dụng phương thức update của JdbcTemplate để thực hiện câu lệnh SQL chèn một bản ghi mới vào bảng T_REWARD với các giá trị tương ứng từ confirmationNumber, contribution và dining
				// - confirmationNumber: Sử dụng biến confirmationNumber đã được tạo ở trên
				// - contribution.getAmount().asBigDecimal(): Lấy số tiền thưởng từ đối tượng contribution và chuyển đổi nó thành BigDecimal
				// - new Date(SimpleDate.today().inMilliseconds()): Lấy ngày hiện tại và chuyển đổi nó thành java.sql.Date
				// - contribution.getAccountNumber(): Lấy số tài khoản từ đối tượng contribution
				// - dining.getMerchantNumber(): Lấy số thương nhân từ đối tượng dining
				// - new Date(dining.getDate().inMilliseconds()): Lấy ngày từ đối tượng dining và chuyển đổi nó thành java.sql.Date
				// - dining.getAmount().asBigDecimal(): Lấy số tiền từ đối tượng dining và chuyển đổi nó thành BigDecimal	
		
		return new RewardConfirmation(confirmationNumber, contribution);
	}

	private String nextConfirmationNumber() {
		String sql = "select next value for S_REWARD_CONFIRMATION_NUMBER from DUAL_REWARD_CONFIRMATION_NUMBER";

		return jdbcTemplate.queryForObject(sql, String.class);
		// Sử dụng phương thức queryForObject của JdbcTemplate để truy vấn một giá trị duy nhất từ cơ sở dữ liệu dựa trên câu lệnh SQL đã cho và trả về kết quả dưới dạng String
		// - sql: Câu lệnh SQL để lấy giá trị tiếp theo của S_REWARD_CONFIRMATION_NUMBER từ bảng DUAL_REWARD_CONFIRMATION_NUMBER
		// - String.class: Chỉ định kiểu dữ liệu của kết quả trả về là String
	}
}
