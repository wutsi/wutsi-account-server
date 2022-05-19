package com.wutsi.platform.account.service.metric

import com.wutsi.platform.core.storage.StorageService
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class ConversionImporter(
    ds: DataSource,
    storage: StorageService,
) : AbstractConversionImporter(ds, storage)
