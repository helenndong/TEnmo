package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.Assert;
import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private static final Transfer TRANSFER_1 = new Transfer(3001, 1, 1, 2001, 2002, new BigDecimal(100));
    private static final Transfer TRANSFER_2 = new Transfer(3002, 2, 2, 2002, 2003, new BigDecimal(200));
    private static final Transfer TRANSFER_3 = new Transfer(3003, 1, 3, 2003, 2001, new BigDecimal(300));
    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void testGetAllTransferByAccountId() {

        List<Transfer> transfers = sut.getAllTransferByAccountId(TRANSFER_2.getAccountFrom());

        Assert.assertNotNull(transfers);
        Assert.assertEquals(2, transfers.size());
    }


    @Test
    public void testGetAllPendingTransfers() {
        List<Transfer> pendingTransfers = sut.getAllPendingTransfers();

        Assert.assertNotNull(pendingTransfers);
        Assert.assertEquals(1, pendingTransfers.size());
    }

    @Test
    public void testDeleteTransfer() {
        int transferId = TRANSFER_3.getId();

        sut.deleteTransfer(transferId);

        Transfer deletedTransfer = sut.getTransferById(transferId);
        Assert.assertNull(deletedTransfer);
    }

    @Test
    public void testUpdateTransferStatus() {
        int transferId = TRANSFER_1.getId();
        int newStatusId = 2;

        sut.updateTransferStatus(transferId, newStatusId);

        Transfer updatedTransfer = sut.getTransferById(transferId);
        Assert.assertNotNull(updatedTransfer);
        Assert.assertEquals(newStatusId, updatedTransfer.getTransferStatusId());
    }

    @Test
    public void getTransferById_given_valid_transfer_id_returns_transfer() {
        Transfer actualTransfer = sut.getTransferById(TRANSFER_1.getId());
        Assert.assertEquals(TRANSFER_1, actualTransfer);
    }

    @Test
    public void testCreateTransfer() {
        Transfer transfer = new Transfer(3001, 1, 2, 2001, 2002, new BigDecimal(50.00));

        Transfer newTransfer = sut.createTransfer(transfer);

        Assert.assertNotNull(newTransfer);
        Assert.assertNotEquals(0, newTransfer.getId());
        Assert.assertEquals(transfer.getAccountFrom(), newTransfer.getAccountFrom());
        Assert.assertEquals(transfer.getAccountTo(), newTransfer.getAccountTo());
        Assert.assertEquals(0, transfer.getAmount().compareTo(newTransfer.getAmount()));

    }




}
