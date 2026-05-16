package com.equipok.DAO;

import com.equipok.model.Waste;
import java.util.List;

public interface IWasteDAO {
    boolean addWaste(Waste waste);
    List<Waste> getWastes();
}
