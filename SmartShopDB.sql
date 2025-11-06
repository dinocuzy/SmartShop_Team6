
USE master;
GO

-- Nếu có kết nối đang mở, buộc ngắt trước khi xóa
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'SmartShopDB')
BEGIN
    ALTER DATABASE SmartShopDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SmartShopDB;
END
GO

-- Tạo mới database hoàn toàn sạch
CREATE DATABASE SmartShopDB;
GO

USE SmartShopDB;
GO
-- ========== DROP TABLES (children first) ==========
DROP TABLE IF EXISTS dbo.OrderStatusHistory;
GO
DROP TABLE IF EXISTS dbo.OrderItems;
GO
DROP TABLE IF EXISTS dbo.Payments;
GO
DROP TABLE IF EXISTS dbo.PromotionProducts;
GO
DROP TABLE IF EXISTS dbo.ProductImages;
GO
DROP TABLE IF EXISTS dbo.ProductViews;
GO
DROP TABLE IF EXISTS dbo.SocialShares;
GO
DROP TABLE IF EXISTS dbo.UserFavorites;
GO
DROP TABLE IF EXISTS dbo.WishlistItems;
GO
DROP TABLE IF EXISTS dbo.Wishlists;
GO
DROP TABLE IF EXISTS dbo.CompareListItems;
GO
DROP TABLE IF EXISTS dbo.CompareLists;
GO
DROP TABLE IF EXISTS dbo.Notifications;
GO
DROP TABLE IF EXISTS dbo.SupportRequests;
GO
DROP TABLE IF EXISTS dbo.NewsletterSubscriptions;
GO
DROP TABLE IF EXISTS dbo.Orders;
GO
DROP TABLE IF EXISTS dbo.Products;
GO
DROP TABLE IF EXISTS dbo.Categories;
GO
DROP TABLE IF EXISTS dbo.PaymentMethods;
GO
DROP TABLE IF EXISTS dbo.Addresses;
GO
DROP TABLE IF EXISTS dbo.UserOAuth;
GO
DROP TABLE IF EXISTS dbo.OAuthProviders;
GO
DROP TABLE IF EXISTS dbo.Users;
GO
DROP TABLE IF EXISTS dbo.Roles;
GO



-- Roles
CREATE TABLE dbo.Roles (
  RoleID INT IDENTITY(1,1) PRIMARY KEY,
  RoleName NVARCHAR(100) NOT NULL UNIQUE,
  Description NVARCHAR(255) NULL
);
GO

-- Users
CREATE TABLE dbo.Users (
  UserID INT IDENTITY(1,1) PRIMARY KEY,
  FullName NVARCHAR(255) NOT NULL,
  Email NVARCHAR(255) NOT NULL UNIQUE,
  PasswordHash NVARCHAR(255) NOT NULL,
  Phone NVARCHAR(30) NULL,
  RoleID INT NOT NULL,
  CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_Users_CreatedAt DEFAULT SYSDATETIME(),
  IsActive BIT NOT NULL CONSTRAINT DF_Users_IsActive DEFAULT 1,
  CONSTRAINT FK_Users_Roles FOREIGN KEY(RoleID) REFERENCES dbo.Roles(RoleID) ON DELETE CASCADE
);
GO

-- OAuth Providers
CREATE TABLE dbo.OAuthProviders (
  ProviderID INT IDENTITY(1,1) PRIMARY KEY,
  ProviderName NVARCHAR(100) NOT NULL UNIQUE
);
GO

-- User OAuth
CREATE TABLE dbo.UserOAuth (
  UserOAuthID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  ProviderID INT NOT NULL,
  ProviderUserID NVARCHAR(255) NOT NULL,
  AccessToken NVARCHAR(500) NULL,
  RefreshToken NVARCHAR(500) NULL,
  LinkedAt DATETIME2 NOT NULL CONSTRAINT DF_UserOAuth_LinkedAt DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_UserOAuth UNIQUE(UserID, ProviderID),
  CONSTRAINT FK_UserOAuth_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE,
  CONSTRAINT FK_UserOAuth_Providers FOREIGN KEY(ProviderID) REFERENCES dbo.OAuthProviders(ProviderID) ON DELETE CASCADE
);
GO

-- Addresses
CREATE TABLE dbo.Addresses (
  AddressID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  FullName NVARCHAR(255) NOT NULL,
  Phone NVARCHAR(30) NOT NULL,
  Line1 NVARCHAR(255) NOT NULL,
  Line2 NVARCHAR(255) NULL,
  City NVARCHAR(100) NOT NULL,
  District NVARCHAR(100) NULL,
  Ward NVARCHAR(100) NULL,
  Country NVARCHAR(100) NOT NULL,
  PostalCode NVARCHAR(20) NULL,
  IsDefault BIT NOT NULL CONSTRAINT DF_Addresses_IsDefault DEFAULT 0,
  CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_Addresses_CreatedAt DEFAULT SYSDATETIME(),
  CONSTRAINT FK_Addresses_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
);
GO

-- Categories
CREATE TABLE dbo.Categories (
  CategoryID INT IDENTITY(1,1) PRIMARY KEY,
  CategoryName NVARCHAR(255) NOT NULL UNIQUE,
  Description NVARCHAR(500) NULL,
  ImageUrl NVARCHAR(500) NULL,
  CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_Categories_CreatedAt DEFAULT SYSDATETIME()
);
GO

-- Payment Methods
CREATE TABLE dbo.PaymentMethods (
  PaymentMethodID INT IDENTITY(1,1) PRIMARY KEY,
  MethodName NVARCHAR(100) NOT NULL UNIQUE,
  Provider NVARCHAR(100) NULL,
  IsActive BIT NOT NULL CONSTRAINT DF_PaymentMethods_IsActive DEFAULT 1
);
GO

-- Products
CREATE TABLE dbo.Products (
  ProductID INT IDENTITY(1,1) PRIMARY KEY,
  CategoryID INT NOT NULL,
  ProductName NVARCHAR(255) NOT NULL,
  Slug NVARCHAR(255) NULL UNIQUE,
  Description NVARCHAR(MAX) NULL,
  Price DECIMAL(12,2) NOT NULL,
  Size NVARCHAR(100) NULL,
  Color NVARCHAR(100) NULL,
  IsSpecial BIT NOT NULL CONSTRAINT DF_Products_IsSpecial DEFAULT 0,
  Stock INT NOT NULL CONSTRAINT DF_Products_Stock DEFAULT 0,
  StockStatus NVARCHAR(50) NULL,
  ImageUrl NVARCHAR(500) NULL,
  CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_Products_CreatedAt DEFAULT SYSDATETIME(),
  UpdatedAt DATETIME2 NULL,
  CONSTRAINT FK_Products_Categories FOREIGN KEY(CategoryID) REFERENCES dbo.Categories(CategoryID) ON DELETE CASCADE
);
GO

-- ProductImages
CREATE TABLE dbo.ProductImages (
  ImageID INT IDENTITY(1,1) PRIMARY KEY,
  ProductID INT NOT NULL,
  ImageUrl NVARCHAR(500) NOT NULL,
  SortOrder INT NOT NULL CONSTRAINT DF_ProductImages_Sort DEFAULT 0,
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_ProductImages_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
);
GO

-- Orders
CREATE TABLE dbo.Orders (
  OrderID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  BillingAddressID INT NULL,
  ShippingAddressID INT NULL,
  OrderStatus NVARCHAR(50) NOT NULL,
  OrderDate DATETIME2 NOT NULL CONSTRAINT DF_Orders_OrderDate DEFAULT SYSDATETIME(),
  TotalAmount DECIMAL(12,2) NOT NULL CONSTRAINT DF_Orders_Total DEFAULT 0,
  Note NVARCHAR(500) NULL,
  CONSTRAINT FK_Orders_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE,
  CONSTRAINT FK_Orders_BillingAddress FOREIGN KEY(BillingAddressID) REFERENCES dbo.Addresses(AddressID),
  CONSTRAINT FK_Orders_ShippingAddress FOREIGN KEY(ShippingAddressID) REFERENCES dbo.Addresses(AddressID)
);
GO

-- OrderItems
CREATE TABLE dbo.OrderItems (
  OrderItemID INT IDENTITY(1,1) PRIMARY KEY,
  OrderID INT NOT NULL,
  ProductID INT NOT NULL,
  Quantity INT NOT NULL CHECK (Quantity > 0),
  UnitPrice DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_OrderItems_Orders FOREIGN KEY(OrderID) REFERENCES dbo.Orders(OrderID) ON DELETE CASCADE,
  CONSTRAINT FK_OrderItems_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID)
);
GO

-- Payments
CREATE TABLE dbo.Payments (
  PaymentID INT IDENTITY(1,1) PRIMARY KEY,
  OrderID INT NOT NULL,
  PaymentMethodID INT NOT NULL,
  Amount DECIMAL(12,2) NOT NULL,
  PaymentStatus NVARCHAR(50) NOT NULL,
  PaymentDate DATETIME2 NOT NULL CONSTRAINT DF_Payments_Date DEFAULT SYSDATETIME(),
  TransactionCode NVARCHAR(255) NULL,
  CONSTRAINT FK_Payments_Orders FOREIGN KEY(OrderID) REFERENCES dbo.Orders(OrderID) ON DELETE CASCADE,
  CONSTRAINT FK_Payments_Methods FOREIGN KEY(PaymentMethodID) REFERENCES dbo.PaymentMethods(PaymentMethodID)
);
GO

-- Order Status History
CREATE TABLE dbo.OrderStatusHistory (
  HistoryID INT IDENTITY(1,1) PRIMARY KEY,
  OrderID INT NOT NULL,
  OldStatus NVARCHAR(50) NOT NULL,
  NewStatus NVARCHAR(50) NOT NULL,
  ChangedAt DATETIME2 NOT NULL CONSTRAINT DF_OrderStatusHistory_ChangedAt DEFAULT SYSDATETIME(),
  ChangedBy INT NULL,
  CONSTRAINT FK_OrderStatusHistory_Orders FOREIGN KEY(OrderID) REFERENCES dbo.Orders(OrderID) ON DELETE CASCADE,
  CONSTRAINT FK_OrderStatusHistory_Users FOREIGN KEY(ChangedBy) REFERENCES dbo.Users(UserID)
);
GO

-- Promotions
CREATE TABLE dbo.Promotions (
  PromotionID INT IDENTITY(1,1) PRIMARY KEY,
  Title NVARCHAR(255) NOT NULL,
  Description NVARCHAR(1000) NULL,
  DiscountPercent DECIMAL(5,2) NULL,
  DiscountAmount DECIMAL(12,2) NULL,
  StartDate DATETIME2 NOT NULL,
  EndDate DATETIME2 NOT NULL,
  IsActive BIT NOT NULL CONSTRAINT DF_Promotions_IsActive DEFAULT 1
);
GO

-- PromotionProducts
CREATE TABLE dbo.PromotionProducts (
  PromotionProductID INT IDENTITY(1,1) PRIMARY KEY,
  PromotionID INT NOT NULL,
  ProductID INT NOT NULL,
  CONSTRAINT UQ_Promotion_Product UNIQUE (PromotionID, ProductID),
  CONSTRAINT FK_PromotionProducts_Promotions FOREIGN KEY(PromotionID) REFERENCES dbo.Promotions(PromotionID) ON DELETE CASCADE,
  CONSTRAINT FK_PromotionProducts_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
);
GO

-- Wishlists + Items
CREATE TABLE dbo.Wishlists (
  WishlistID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_Wishlist_User UNIQUE(UserID),
  CONSTRAINT FK_Wishlists_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
);
GO

CREATE TABLE dbo.WishlistItems (
  WishlistItemID INT IDENTITY(1,1) PRIMARY KEY,
  WishlistID INT NOT NULL,
  ProductID INT NOT NULL,
  AddedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_WishlistItem UNIQUE(WishlistID, ProductID),
  CONSTRAINT FK_WishlistItems_Wishlists FOREIGN KEY(WishlistID) REFERENCES dbo.Wishlists(WishlistID) ON DELETE CASCADE,
  CONSTRAINT FK_WishlistItems_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
);
GO

-- CompareLists + Items
CREATE TABLE dbo.CompareLists (
  CompareListID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_CompareList_User UNIQUE(UserID),
  CONSTRAINT FK_CompareLists_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
);
GO

CREATE TABLE dbo.CompareListItems (
  CompareListItemID INT IDENTITY(1,1) PRIMARY KEY,
  CompareListID INT NOT NULL,
  ProductID INT NOT NULL,
  AddedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_CompareListItem UNIQUE(CompareListID, ProductID),
  CONSTRAINT FK_CompareListItems_CompareLists FOREIGN KEY(CompareListID) REFERENCES dbo.CompareLists(CompareListID) ON DELETE CASCADE,
  CONSTRAINT FK_CompareListItems_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
);
GO

-- Notifications
CREATE TABLE dbo.Notifications (
  NotificationID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  Title NVARCHAR(255) NOT NULL,
  Content NVARCHAR(1000) NOT NULL,
  IsRead BIT NOT NULL DEFAULT 0,
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_Notifications_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
);
GO

-- SupportRequests
CREATE TABLE dbo.SupportRequests (
  RequestID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  Subject NVARCHAR(255) NOT NULL,
  Message NVARCHAR(2000) NOT NULL,
  Status NVARCHAR(50) NOT NULL DEFAULT 'Open',
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_SupportRequests_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
);
GO

-- NewsletterSubscriptions
CREATE TABLE dbo.NewsletterSubscriptions (
  SubscriptionID INT IDENTITY(1,1) PRIMARY KEY,
  Email NVARCHAR(255) NOT NULL UNIQUE,
  SubscribedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ProductViews
CREATE TABLE dbo.ProductViews (
  ViewID INT IDENTITY(1,1) PRIMARY KEY,
  ProductID INT NOT NULL,
  UserID INT NULL,
  ViewedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_ProductViews_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT FK_ProductViews_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID)
);
GO

-- SocialShares
CREATE TABLE dbo.SocialShares (
  ShareID INT IDENTITY(1,1) PRIMARY KEY,
  ProductID INT NOT NULL,
  UserID INT NULL,
  Platform NVARCHAR(50) NOT NULL,
  SharedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT FK_SocialShares_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT FK_SocialShares_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID)
);
GO

-- UserFavorites
CREATE TABLE dbo.UserFavorites (
  FavoriteID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  ProductID INT NOT NULL,
  CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  CONSTRAINT UQ_UserFavorites_User_Product UNIQUE(UserID, ProductID),
  CONSTRAINT FK_UserFavorites_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE,
  CONSTRAINT FK_UserFavorites_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE
);
GO

-- Indexes
CREATE INDEX IX_Products_Category ON dbo.Products(CategoryID);
GO
CREATE INDEX IX_OrderItems_Order ON dbo.OrderItems(OrderID);
GO
CREATE INDEX IX_Payments_Order ON dbo.Payments(OrderID);
GO
CREATE INDEX IX_ProductViews_Product ON dbo.ProductViews(ProductID);
GO
CREATE INDEX IX_SocialShares_Product ON dbo.SocialShares(ProductID);
GO
CREATE INDEX IX_UserFavorites_User ON dbo.UserFavorites(UserID);
GO
CREATE INDEX IX_CompareListItems_List ON dbo.CompareListItems(CompareListID);
GO


--nap dữ liệu mẫu--
USE SmartShopDB;
GO
-- Roles
IF NOT EXISTS (SELECT 1 FROM dbo.Roles WHERE RoleName=N'Admin') INSERT INTO dbo.Roles(RoleName, Description) VALUES (N'Admin', N'Full access');
IF NOT EXISTS (SELECT 1 FROM dbo.Roles WHERE RoleName=N'Manager') INSERT INTO dbo.Roles(RoleName, Description) VALUES (N'Manager', N'Manage catalog & orders');
IF NOT EXISTS (SELECT 1 FROM dbo.Roles WHERE RoleName=N'Staff') INSERT INTO dbo.Roles(RoleName, Description) VALUES (N'Staff', N'Support & operations');
IF NOT EXISTS (SELECT 1 FROM dbo.Roles WHERE RoleName=N'Customer') INSERT INTO dbo.Roles(RoleName, Description) VALUES (N'Customer', N'Default customer role');
GO
-- Users
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'admin@smartshop.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Nguyen Van A', N'admin@smartshop.local', N'hash_admin', N'0900000001', 1);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'manager@smartshop.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Tran Thi B', N'manager@smartshop.local', N'hash_manager', N'0900000002', 2);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'staff1@smartshop.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Le Van C', N'staff1@smartshop.local', N'hash_staff1', N'0900000003', 3);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'staff2@smartshop.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Pham Thi D', N'staff2@smartshop.local', N'hash_staff2', N'0900000004', 3);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer1@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 1', N'customer1@mail.local', N'hash_cust1', N'0912300001', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer2@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 2', N'customer2@mail.local', N'hash_cust2', N'0912300002', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer3@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 3', N'customer3@mail.local', N'hash_cust3', N'0912300003', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer4@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 4', N'customer4@mail.local', N'hash_cust4', N'0912300004', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer5@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 5', N'customer5@mail.local', N'hash_cust5', N'0912300005', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer6@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 6', N'customer6@mail.local', N'hash_cust6', N'0912300006', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer7@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 7', N'customer7@mail.local', N'hash_cust7', N'0912300007', 4);
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'customer8@mail.local') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Customer 8', N'customer8@mail.local', N'hash_cust8', N'0912300008', 4);
GO
-- OAuth Providers
IF NOT EXISTS (SELECT 1 FROM dbo.OAuthProviders WHERE ProviderName=N'Google') INSERT INTO dbo.OAuthProviders(ProviderName) VALUES (N'Google');
IF NOT EXISTS (SELECT 1 FROM dbo.OAuthProviders WHERE ProviderName=N'Facebook') INSERT INTO dbo.OAuthProviders(ProviderName) VALUES (N'Facebook');
IF NOT EXISTS (SELECT 1 FROM dbo.OAuthProviders WHERE ProviderName=N'GitHub') INSERT INTO dbo.OAuthProviders(ProviderName) VALUES (N'GitHub');
IF NOT EXISTS (SELECT 1 FROM dbo.OAuthProviders WHERE ProviderName=N'Microsoft') INSERT INTO dbo.OAuthProviders(ProviderName) VALUES (N'Microsoft');
GO
-- Payment Methods
IF NOT EXISTS (SELECT 1 FROM dbo.PaymentMethods WHERE MethodName=N'Cash') INSERT INTO dbo.PaymentMethods(MethodName, Provider, IsActive) VALUES (N'Cash', NULL, 1);
IF NOT EXISTS (SELECT 1 FROM dbo.PaymentMethods WHERE MethodName=N'Bank Transfer') INSERT INTO dbo.PaymentMethods(MethodName, Provider, IsActive) VALUES (N'Bank Transfer', N'Vietcombank', 1);
IF NOT EXISTS (SELECT 1 FROM dbo.PaymentMethods WHERE MethodName=N'VNPay') INSERT INTO dbo.PaymentMethods(MethodName, Provider, IsActive) VALUES (N'VNPay', N'VNPay', 1);
IF NOT EXISTS (SELECT 1 FROM dbo.PaymentMethods WHERE MethodName=N'MoMo') INSERT INTO dbo.PaymentMethods(MethodName, Provider, IsActive) VALUES (N'MoMo', N'MoMo', 1);
GO
-- Categories
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Điện thoại') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Điện thoại', N'Mô tả Điện thoại', N'/img/điện-thoại.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Laptop') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Laptop', N'Mô tả Laptop', N'/img/laptop.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Tablet') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Tablet', N'Mô tả Tablet', N'/img/tablet.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Phụ kiện') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Phụ kiện', N'Mô tả Phụ kiện', N'/img/phụ-kiện.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Âm thanh') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Âm thanh', N'Mô tả Âm thanh', N'/img/âm-thanh.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Smartwatch') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Smartwatch', N'Mô tả Smartwatch', N'/img/smartwatch.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Thiết bị mạng') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Thiết bị mạng', N'Mô tả Thiết bị mạng', N'/img/thiết-bị-mạng.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Gaming Gear') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Gaming Gear', N'Mô tả Gaming Gear', N'/img/gaming-gear.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'TV & Monitor') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'TV & Monitor', N'Mô tả TV & Monitor', N'/img/tv-&-monitor.jpg');
IF NOT EXISTS (SELECT 1 FROM dbo.Categories WHERE CategoryName=N'Nhà thông minh') INSERT INTO dbo.Categories(CategoryName, Description, ImageUrl) VALUES (N'Nhà thông minh', N'Mô tả Nhà thông minh', N'/img/nhà-thông-minh.jpg');
GO
-- Products
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 1', N'điện-thoại-mẫu-1', N'Mô tả Điện thoại mẫu 1', 900.0, N'Đỏ', 68, N'InStock', N'/img/điện-thoại-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 2', N'điện-thoại-mẫu-2', N'Mô tả Điện thoại mẫu 2', 300.0, N'Đen', 78, N'InStock', N'/img/điện-thoại-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 3', N'điện-thoại-mẫu-3', N'Mô tả Điện thoại mẫu 3', 340.0, N'Đen', 23, N'InStock', N'/img/điện-thoại-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 4', N'điện-thoại-mẫu-4', N'Mô tả Điện thoại mẫu 4', 990.0, N'Đỏ', 51, N'InStock', N'/img/điện-thoại-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 5', N'điện-thoại-mẫu-5', N'Mô tả Điện thoại mẫu 5', 890.0, N'Đỏ', 56, N'InStock', N'/img/điện-thoại-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Điện thoại mẫu 6', N'điện-thoại-mẫu-6', N'Mô tả Điện thoại mẫu 6', 500.0, N'Bạc', 39, N'InStock', N'/img/điện-thoại-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 1', N'laptop-mẫu-1', N'Mô tả Laptop mẫu 1', 570.0, N'Trắng', 94, N'InStock', N'/img/laptop-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 2', N'laptop-mẫu-2', N'Mô tả Laptop mẫu 2', 710.0, N'Đen', 76, N'InStock', N'/img/laptop-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 3', N'laptop-mẫu-3', N'Mô tả Laptop mẫu 3', 1170.0, N'Trắng', 79, N'InStock', N'/img/laptop-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 4', N'laptop-mẫu-4', N'Mô tả Laptop mẫu 4', 1110.0, N'Bạc', 85, N'InStock', N'/img/laptop-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 5', N'laptop-mẫu-5', N'Mô tả Laptop mẫu 5', 750.0, N'Đen', 20, N'InStock', N'/img/laptop-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Laptop mẫu 6', N'laptop-mẫu-6', N'Mô tả Laptop mẫu 6', 270.0, N'Trắng', 26, N'InStock', N'/img/laptop-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 1', N'tablet-mẫu-1', N'Mô tả Tablet mẫu 1', 510.0, N'Đỏ', 36, N'InStock', N'/img/tablet-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 2', N'tablet-mẫu-2', N'Mô tả Tablet mẫu 2', 1240.0, N'Bạc', 73, N'InStock', N'/img/tablet-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 3', N'tablet-mẫu-3', N'Mô tả Tablet mẫu 3', 900.0, N'Trắng', 63, N'InStock', N'/img/tablet-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 4', N'tablet-mẫu-4', N'Mô tả Tablet mẫu 4', 590.0, N'Xanh', 39, N'InStock', N'/img/tablet-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 5', N'tablet-mẫu-5', N'Mô tả Tablet mẫu 5', 1050.0, N'Bạc', 73, N'InStock', N'/img/tablet-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Tablet mẫu 6', N'tablet-mẫu-6', N'Mô tả Tablet mẫu 6', 820.0, N'Đen', 19, N'InStock', N'/img/tablet-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 1', N'phụ-kiện-mẫu-1', N'Mô tả Phụ kiện mẫu 1', 650.0, N'Xanh', 33, N'InStock', N'/img/phụ-kiện-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 2', N'phụ-kiện-mẫu-2', N'Mô tả Phụ kiện mẫu 2', 620.0, N'Đen', 23, N'InStock', N'/img/phụ-kiện-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 3', N'phụ-kiện-mẫu-3', N'Mô tả Phụ kiện mẫu 3', 1330.0, N'Xanh', 67, N'InStock', N'/img/phụ-kiện-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 4', N'phụ-kiện-mẫu-4', N'Mô tả Phụ kiện mẫu 4', 1230.0, N'Trắng', 35, N'InStock', N'/img/phụ-kiện-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 5', N'phụ-kiện-mẫu-5', N'Mô tả Phụ kiện mẫu 5', 410.0, N'Trắng', 81, N'InStock', N'/img/phụ-kiện-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Phụ kiện mẫu 6', N'phụ-kiện-mẫu-6', N'Mô tả Phụ kiện mẫu 6', 750.0, N'Trắng', 91, N'InStock', N'/img/phụ-kiện-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 1', N'âm-thanh-mẫu-1', N'Mô tả Âm thanh mẫu 1', 550.0, N'Trắng', 18, N'InStock', N'/img/âm-thanh-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 2', N'âm-thanh-mẫu-2', N'Mô tả Âm thanh mẫu 2', 710.0, N'Xanh', 44, N'InStock', N'/img/âm-thanh-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 3', N'âm-thanh-mẫu-3', N'Mô tả Âm thanh mẫu 3', 750.0, N'Trắng', 54, N'InStock', N'/img/âm-thanh-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 4', N'âm-thanh-mẫu-4', N'Mô tả Âm thanh mẫu 4', 1200.0, N'Đen', 92, N'InStock', N'/img/âm-thanh-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 5', N'âm-thanh-mẫu-5', N'Mô tả Âm thanh mẫu 5', 740.0, N'Đen', 64, N'InStock', N'/img/âm-thanh-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Âm thanh mẫu 6', N'âm-thanh-mẫu-6', N'Mô tả Âm thanh mẫu 6', 920.0, N'Trắng', 73, N'InStock', N'/img/âm-thanh-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 1', N'smartwatch-mẫu-1', N'Mô tả Smartwatch mẫu 1', 1000.0, N'Đen', 53, N'InStock', N'/img/smartwatch-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 2', N'smartwatch-mẫu-2', N'Mô tả Smartwatch mẫu 2', 1020.0, N'Đỏ', 29, N'InStock', N'/img/smartwatch-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 3', N'smartwatch-mẫu-3', N'Mô tả Smartwatch mẫu 3', 1460.0, N'Đỏ', 59, N'InStock', N'/img/smartwatch-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 4', N'smartwatch-mẫu-4', N'Mô tả Smartwatch mẫu 4', 820.0, N'Đen', 84, N'InStock', N'/img/smartwatch-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 5', N'smartwatch-mẫu-5', N'Mô tả Smartwatch mẫu 5', 1420.0, N'Xanh', 7, N'InStock', N'/img/smartwatch-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Smartwatch mẫu 6', N'smartwatch-mẫu-6', N'Mô tả Smartwatch mẫu 6', 840.0, N'Bạc', 76, N'InStock', N'/img/smartwatch-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 1', N'thiết-bị-mạng-mẫu-1', N'Mô tả Thiết bị mạng mẫu 1', 1300.0, N'Đỏ', 23, N'InStock', N'/img/thiết-bị-mạng-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 2', N'thiết-bị-mạng-mẫu-2', N'Mô tả Thiết bị mạng mẫu 2', 1020.0, N'Xanh', 87, N'InStock', N'/img/thiết-bị-mạng-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 3', N'thiết-bị-mạng-mẫu-3', N'Mô tả Thiết bị mạng mẫu 3', 890.0, N'Xanh', 68, N'InStock', N'/img/thiết-bị-mạng-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 4', N'thiết-bị-mạng-mẫu-4', N'Mô tả Thiết bị mạng mẫu 4', 1630.0, N'Đen', 26, N'InStock', N'/img/thiết-bị-mạng-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 5', N'thiết-bị-mạng-mẫu-5', N'Mô tả Thiết bị mạng mẫu 5', 1070.0, N'Xanh', 20, N'InStock', N'/img/thiết-bị-mạng-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Thiết bị mạng mẫu 6', N'thiết-bị-mạng-mẫu-6', N'Mô tả Thiết bị mạng mẫu 6', 1350.0, N'Trắng', 61, N'InStock', N'/img/thiết-bị-mạng-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 1', N'gaming-gear-mẫu-1', N'Mô tả Gaming Gear mẫu 1', 1210.0, N'Đỏ', 68, N'InStock', N'/img/gaming-gear-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 2', N'gaming-gear-mẫu-2', N'Mô tả Gaming Gear mẫu 2', 1350.0, N'Đen', 72, N'InStock', N'/img/gaming-gear-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 3', N'gaming-gear-mẫu-3', N'Mô tả Gaming Gear mẫu 3', 1040.0, N'Bạc', 37, N'InStock', N'/img/gaming-gear-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 4', N'gaming-gear-mẫu-4', N'Mô tả Gaming Gear mẫu 4', 1330.0, N'Bạc', 76, N'InStock', N'/img/gaming-gear-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 5', N'gaming-gear-mẫu-5', N'Mô tả Gaming Gear mẫu 5', 950.0, N'Xanh', 86, N'InStock', N'/img/gaming-gear-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Gaming Gear mẫu 6', N'gaming-gear-mẫu-6', N'Mô tả Gaming Gear mẫu 6', 1160.0, N'Trắng', 93, N'InStock', N'/img/gaming-gear-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 1', N'tv-&-monitor-mẫu-1', N'Mô tả TV & Monitor mẫu 1', 970.0, N'Bạc', 21, N'InStock', N'/img/tv-&-monitor-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 2', N'tv-&-monitor-mẫu-2', N'Mô tả TV & Monitor mẫu 2', 1420.0, N'Đen', 65, N'InStock', N'/img/tv-&-monitor-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 3', N'tv-&-monitor-mẫu-3', N'Mô tả TV & Monitor mẫu 3', 1790.0, N'Đen', 34, N'InStock', N'/img/tv-&-monitor-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 4', N'tv-&-monitor-mẫu-4', N'Mô tả TV & Monitor mẫu 4', 1680.0, N'Xanh', 7, N'InStock', N'/img/tv-&-monitor-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 5', N'tv-&-monitor-mẫu-5', N'Mô tả TV & Monitor mẫu 5', 1100.0, N'Đen', 80, N'InStock', N'/img/tv-&-monitor-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'TV & Monitor mẫu 6', N'tv-&-monitor-mẫu-6', N'Mô tả TV & Monitor mẫu 6', 1420.0, N'Trắng', 36, N'InStock', N'/img/tv-&-monitor-mẫu-6.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 1', N'nhà-thông-minh-mẫu-1', N'Mô tả Nhà thông minh mẫu 1', 1730.0, N'Trắng', 31, N'InStock', N'/img/nhà-thông-minh-mẫu-1.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 2', N'nhà-thông-minh-mẫu-2', N'Mô tả Nhà thông minh mẫu 2', 1220.0, N'Trắng', 53, N'InStock', N'/img/nhà-thông-minh-mẫu-2.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 3', N'nhà-thông-minh-mẫu-3', N'Mô tả Nhà thông minh mẫu 3', 1590.0, N'Đỏ', 22, N'InStock', N'/img/nhà-thông-minh-mẫu-3.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 4', N'nhà-thông-minh-mẫu-4', N'Mô tả Nhà thông minh mẫu 4', 1090.0, N'Trắng', 12, N'InStock', N'/img/nhà-thông-minh-mẫu-4.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 5', N'nhà-thông-minh-mẫu-5', N'Mô tả Nhà thông minh mẫu 5', 1760.0, N'Xanh', 29, N'InStock', N'/img/nhà-thông-minh-mẫu-5.jpg');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Nhà thông minh mẫu 6', N'nhà-thông-minh-mẫu-6', N'Mô tả Nhà thông minh mẫu 6', 1730.0, N'Xanh', 18, N'InStock', N'/img/nhà-thông-minh-mẫu-6.jpg');
GO
-- Promotions
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 30%', N'Khuyến mãi 30%', 30, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 15%', N'Khuyến mãi 15%', 15, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 25%', N'Khuyến mãi 25%', 25, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 5%', N'Khuyến mãi 5%', 5, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 10%', N'Khuyến mãi 10%', 10, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 25%', N'Khuyến mãi 25%', 25, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 10%', N'Khuyến mãi 10%', 10, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 5%', N'Khuyến mãi 5%', 5, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 30%', N'Khuyến mãi 30%', 30, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
INSERT INTO dbo.Promotions(Title, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (N'Discount 25%', N'Khuyến mãi 25%', 25, DATEADD(DAY,-7, SYSDATETIME()), DATEADD(DAY,30, SYSDATETIME()), 1);
GO
-- PromotionProducts
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=1) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 1);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=2) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 2);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=3) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 3);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=4) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 4);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=5) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 5);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=1 AND ProductID=6) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (1, 6);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=7) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 7);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=8) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 8);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=9) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 9);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=10) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 10);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=11) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 11);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=2 AND ProductID=12) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (2, 12);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=13) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 13);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=14) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 14);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=15) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 15);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=16) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 16);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=17) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 17);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=3 AND ProductID=18) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (3, 18);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=19) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 19);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=20) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 20);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=21) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 21);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=22) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 22);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=23) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 23);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=4 AND ProductID=24) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (4, 24);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=25) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 25);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=26) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 26);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=27) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 27);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=28) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 28);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=29) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 29);
IF NOT EXISTS (SELECT 1 FROM dbo.PromotionProducts WHERE PromotionID=5 AND ProductID=30) INSERT INTO dbo.PromotionProducts(PromotionID, ProductID) VALUES (5, 30);
GO
-- Orders
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (6, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (3, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (10, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (9, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (6, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (3, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (9, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (9, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (3, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (7, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (9, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (8, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (7, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (10, NULL, NULL, N'Pending', 0);
INSERT INTO dbo.Orders(UserID, BillingAddressID, ShippingAddressID, OrderStatus, TotalAmount) VALUES (7, NULL, NULL, N'Pending', 0);
GO
-- OrderItems + Update Totals
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (1, 52, 1, 1053);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (1, 7, 3, 1295);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (1, 47, 1, 1478);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=1) FROM dbo.Orders o WHERE o.OrderID=1;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (2, 46, 3, 724);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (2, 8, 1, 964);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (2, 39, 1, 613);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=2) FROM dbo.Orders o WHERE o.OrderID=2;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (3, 41, 2, 299);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (3, 17, 1, 761);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (3, 7, 3, 668);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=3) FROM dbo.Orders o WHERE o.OrderID=3;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (4, 7, 2, 1171);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (4, 33, 3, 1108);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (4, 7, 3, 460);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=4) FROM dbo.Orders o WHERE o.OrderID=4;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (5, 58, 2, 1141);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (5, 20, 1, 1221);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (5, 14, 3, 617);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=5) FROM dbo.Orders o WHERE o.OrderID=5;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (6, 31, 2, 1422);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (6, 16, 1, 1081);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (6, 16, 2, 785);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=6) FROM dbo.Orders o WHERE o.OrderID=6;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (7, 12, 1, 262);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (7, 22, 3, 1252);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (7, 26, 3, 686);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=7) FROM dbo.Orders o WHERE o.OrderID=7;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (8, 48, 2, 1208);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (8, 53, 3, 695);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (8, 15, 2, 1005);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=8) FROM dbo.Orders o WHERE o.OrderID=8;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (9, 21, 3, 1208);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (9, 17, 3, 1078);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (9, 53, 3, 1000);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=9) FROM dbo.Orders o WHERE o.OrderID=9;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (10, 25, 3, 803);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (10, 31, 3, 1469);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (10, 33, 2, 647);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=10) FROM dbo.Orders o WHERE o.OrderID=10;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (11, 34, 2, 1181);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (11, 45, 1, 1120);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (11, 37, 3, 1309);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=11) FROM dbo.Orders o WHERE o.OrderID=11;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (12, 47, 2, 1270);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (12, 56, 3, 970);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (12, 41, 3, 674);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=12) FROM dbo.Orders o WHERE o.OrderID=12;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (13, 59, 1, 1200);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (13, 33, 1, 869);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (13, 44, 3, 862);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=13) FROM dbo.Orders o WHERE o.OrderID=13;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (14, 35, 3, 278);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (14, 9, 2, 503);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (14, 17, 1, 1124);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=14) FROM dbo.Orders o WHERE o.OrderID=14;
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (15, 15, 2, 488);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (15, 46, 1, 1467);
INSERT INTO dbo.OrderItems(OrderID, ProductID, Quantity, UnitPrice) VALUES (15, 15, 2, 699);
UPDATE o SET TotalAmount = (SELECT SUM(Quantity*UnitPrice) FROM dbo.OrderItems WHERE OrderID=15) FROM dbo.Orders o WHERE o.OrderID=15;
GO
-- Payments
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 1, 2, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=1;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 3, 3, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=3;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 5, 4, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=5;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 7, 3, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=7;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 9, 2, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=9;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 11, 3, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=11;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 13, 4, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=13;
INSERT INTO dbo.Payments(OrderID, PaymentMethodID, Amount, PaymentStatus) SELECT 15, 1, TotalAmount, N'Paid' FROM dbo.Orders WHERE OrderID=15;
GO
-- OrderStatusHistory
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (1, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (2, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (3, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (4, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (5, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (6, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (7, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (8, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (9, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (10, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (11, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (12, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (13, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (14, N'Pending', N'Processing');
INSERT INTO dbo.OrderStatusHistory(OrderID, OldStatus, NewStatus) VALUES (15, N'Pending', N'Processing');
GO
-- Wishlists + Items
IF NOT EXISTS (SELECT 1 FROM dbo.Wishlists WHERE UserID=5) INSERT INTO dbo.Wishlists(UserID) VALUES(5);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 8 FROM dbo.Wishlists WHERE UserID=5 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=5 AND wi.ProductID=8);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 1 FROM dbo.Wishlists WHERE UserID=5 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=5 AND wi.ProductID=1);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 37 FROM dbo.Wishlists WHERE UserID=5 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=5 AND wi.ProductID=37);
IF NOT EXISTS (SELECT 1 FROM dbo.Wishlists WHERE UserID=6) INSERT INTO dbo.Wishlists(UserID) VALUES(6);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 4 FROM dbo.Wishlists WHERE UserID=6 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=6 AND wi.ProductID=4);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 54 FROM dbo.Wishlists WHERE UserID=6 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=6 AND wi.ProductID=54);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 29 FROM dbo.Wishlists WHERE UserID=6 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=6 AND wi.ProductID=29);
IF NOT EXISTS (SELECT 1 FROM dbo.Wishlists WHERE UserID=7) INSERT INTO dbo.Wishlists(UserID) VALUES(7);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 40 FROM dbo.Wishlists WHERE UserID=7 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=7 AND wi.ProductID=40);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 2 FROM dbo.Wishlists WHERE UserID=7 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=7 AND wi.ProductID=2);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 53 FROM dbo.Wishlists WHERE UserID=7 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=7 AND wi.ProductID=53);
IF NOT EXISTS (SELECT 1 FROM dbo.Wishlists WHERE UserID=8) INSERT INTO dbo.Wishlists(UserID) VALUES(8);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 53 FROM dbo.Wishlists WHERE UserID=8 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=8 AND wi.ProductID=53);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 51 FROM dbo.Wishlists WHERE UserID=8 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=8 AND wi.ProductID=51);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 14 FROM dbo.Wishlists WHERE UserID=8 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=8 AND wi.ProductID=14);
IF NOT EXISTS (SELECT 1 FROM dbo.Wishlists WHERE UserID=9) INSERT INTO dbo.Wishlists(UserID) VALUES(9);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 55 FROM dbo.Wishlists WHERE UserID=9 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=9 AND wi.ProductID=55);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 5 FROM dbo.Wishlists WHERE UserID=9 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=9 AND wi.ProductID=5);
INSERT INTO dbo.WishlistItems(WishlistID, ProductID) SELECT WishlistID, 54 FROM dbo.Wishlists WHERE UserID=9 AND NOT EXISTS (SELECT 1 FROM dbo.WishlistItems wi JOIN dbo.Wishlists w ON wi.WishlistID=w.WishlistID WHERE w.UserID=9 AND wi.ProductID=54);
GO
-- CompareLists + Items
IF NOT EXISTS (SELECT 1 FROM dbo.CompareLists WHERE UserID=6) INSERT INTO dbo.CompareLists(UserID) VALUES(6);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 58 FROM dbo.CompareLists WHERE UserID=6 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=6 AND ci.ProductID=58);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 13 FROM dbo.CompareLists WHERE UserID=6 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=6 AND ci.ProductID=13);
IF NOT EXISTS (SELECT 1 FROM dbo.CompareLists WHERE UserID=7) INSERT INTO dbo.CompareLists(UserID) VALUES(7);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 34 FROM dbo.CompareLists WHERE UserID=7 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=7 AND ci.ProductID=34);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 5 FROM dbo.CompareLists WHERE UserID=7 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=7 AND ci.ProductID=5);
IF NOT EXISTS (SELECT 1 FROM dbo.CompareLists WHERE UserID=8) INSERT INTO dbo.CompareLists(UserID) VALUES(8);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 27 FROM dbo.CompareLists WHERE UserID=8 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=8 AND ci.ProductID=27);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 1 FROM dbo.CompareLists WHERE UserID=8 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=8 AND ci.ProductID=1);
IF NOT EXISTS (SELECT 1 FROM dbo.CompareLists WHERE UserID=9) INSERT INTO dbo.CompareLists(UserID) VALUES(9);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 15 FROM dbo.CompareLists WHERE UserID=9 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=9 AND ci.ProductID=15);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 38 FROM dbo.CompareLists WHERE UserID=9 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=9 AND ci.ProductID=38);
IF NOT EXISTS (SELECT 1 FROM dbo.CompareLists WHERE UserID=10) INSERT INTO dbo.CompareLists(UserID) VALUES(10);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 22 FROM dbo.CompareLists WHERE UserID=10 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=10 AND ci.ProductID=22);
INSERT INTO dbo.CompareListItems(CompareListID, ProductID) SELECT CompareListID, 18 FROM dbo.CompareLists WHERE UserID=10 AND NOT EXISTS (SELECT 1 FROM dbo.CompareListItems ci JOIN dbo.CompareLists cl ON ci.CompareListID=cl.CompareListID WHERE cl.UserID=10 AND ci.ProductID=18);
GO
-- Notifications & SupportRequests
INSERT INTO dbo.Notifications(UserID, Title, Content) VALUES (3, N'Chào mừng', N'Tài khoản của bạn đã được kích hoạt');
INSERT INTO dbo.SupportRequests(UserID, Subject, Message) VALUES (3, N'Hỗ trợ đơn hàng', N'Tôi cần hỗ trợ về đơn hàng #3');
INSERT INTO dbo.Notifications(UserID, Title, Content) VALUES (4, N'Chào mừng', N'Tài khoản của bạn đã được kích hoạt');
INSERT INTO dbo.SupportRequests(UserID, Subject, Message) VALUES (4, N'Hỗ trợ đơn hàng', N'Tôi cần hỗ trợ về đơn hàng #4');
INSERT INTO dbo.Notifications(UserID, Title, Content) VALUES (5, N'Chào mừng', N'Tài khoản của bạn đã được kích hoạt');
INSERT INTO dbo.SupportRequests(UserID, Subject, Message) VALUES (5, N'Hỗ trợ đơn hàng', N'Tôi cần hỗ trợ về đơn hàng #5');
INSERT INTO dbo.Notifications(UserID, Title, Content) VALUES (6, N'Chào mừng', N'Tài khoản của bạn đã được kích hoạt');
INSERT INTO dbo.SupportRequests(UserID, Subject, Message) VALUES (6, N'Hỗ trợ đơn hàng', N'Tôi cần hỗ trợ về đơn hàng #6');
INSERT INTO dbo.Notifications(UserID, Title, Content) VALUES (7, N'Chào mừng', N'Tài khoản của bạn đã được kích hoạt');
INSERT INTO dbo.SupportRequests(UserID, Subject, Message) VALUES (7, N'Hỗ trợ đơn hàng', N'Tôi cần hỗ trợ về đơn hàng #7');
GO
-- NewsletterSubscriptions
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user1@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user1@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user2@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user2@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user3@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user3@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user4@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user4@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user5@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user5@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user6@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user6@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user7@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user7@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user8@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user8@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user9@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user9@mail.local');
IF NOT EXISTS (SELECT 1 FROM dbo.NewsletterSubscriptions WHERE Email=N'user10@mail.local') INSERT INTO dbo.NewsletterSubscriptions(Email) VALUES (N'user10@mail.local');
GO
-- ProductViews / SocialShares / UserFavorites
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (32, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (48, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (21, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (60, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (34, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (1, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (21, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (28, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (3, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (20, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (44, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (22, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (13, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (4, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (26, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (14, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (42, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (2, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (18, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (25, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (57, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (27, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (57, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (36, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (55, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (42, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (53, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (4, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (18, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (17, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (22, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (20, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (49, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (60, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (53, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (18, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (31, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (10, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (7, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (7, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (31, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (23, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (36, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (52, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (24, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (21, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (35, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (47, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (19, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (30, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (37, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (25, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (48, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (51, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (58, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (46, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (35, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (20, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (4, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (20, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (49, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (24, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (49, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (12, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (52, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (36, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (47, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (37, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (16, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (53, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (3, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (1, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (31, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (21, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (10, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (47, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (43, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (51, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (57, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (52, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (2, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (27, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (58, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (20, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (22, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (32, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (59, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (23, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (16, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (24, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (28, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (8, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (16, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (8, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (27, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (53, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (2, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (58, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (39, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (34, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (14, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (37, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (8, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (22, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (30, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (24, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (10, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (31, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (42, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (9, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (7, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (54, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (38, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (32, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (50, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (44, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (28, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (59, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (42, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (8, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (58, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (56, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (30, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (23, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (43, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (41, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (7, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (25, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (34, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (33, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (3, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (44, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (11, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (33, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (24, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (46, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (27, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (27, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (54, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (44, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (17, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (49, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (49, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (23, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (25, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (28, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (17, 4);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (60, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (30, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (48, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (1, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (8, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (1, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (45, 10);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (26, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (14, 9);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (60, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (36, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (57, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (36, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (21, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 3);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (15, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (6, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (35, 7);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (53, 8);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (40, 5);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (28, 6);
INSERT INTO dbo.ProductViews(ProductID, UserID) VALUES (17, 7);
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (39, 4, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (24, 5, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (17, 3, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (60, 9, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (45, 7, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (51, 10, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (13, 9, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (15, 7, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (18, 4, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (53, 5, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (39, 4, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (22, 8, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (25, 7, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (11, 8, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (42, 9, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (4, 3, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (1, 3, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (37, 3, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (12, 4, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (10, 9, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (44, 8, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (3, 9, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (38, 5, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (47, 3, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (46, 4, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (19, 4, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (4, 3, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (31, 8, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (19, 4, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (41, 3, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (41, 5, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (20, 5, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (40, 5, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (41, 5, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (30, 10, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (1, 4, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (4, 3, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (49, 6, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (52, 8, N'Zalo');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (47, 8, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (50, 8, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (58, 4, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (16, 3, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (17, 10, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (3, 3, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (48, 7, N'Instagram');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (37, 9, N'Facebook');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (21, 4, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (20, 10, N'Twitter');
INSERT INTO dbo.SocialShares(ProductID, UserID, Platform) VALUES (50, 4, N'Zalo');
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=3 AND ProductID=45) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (3, 45);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=5 AND ProductID=14) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (5, 14);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=9 AND ProductID=41) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (9, 41);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=4 AND ProductID=8) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (4, 8);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=5 AND ProductID=22) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (5, 22);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=8 AND ProductID=27) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (8, 27);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=6 AND ProductID=35) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (6, 35);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=7 AND ProductID=24) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (7, 24);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=10 AND ProductID=23) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (10, 23);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=6 AND ProductID=56) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (6, 56);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=4 AND ProductID=57) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (4, 57);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=6 AND ProductID=52) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (6, 52);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=6 AND ProductID=20) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (6, 20);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=8 AND ProductID=15) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (8, 15);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=8 AND ProductID=45) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (8, 45);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=8 AND ProductID=7) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (8, 7);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=8 AND ProductID=40) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (8, 40);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=10 AND ProductID=4) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (10, 4);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=7 AND ProductID=33) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (7, 33);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=9 AND ProductID=29) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (9, 29);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=3 AND ProductID=46) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (3, 46);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=10 AND ProductID=49) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (10, 49);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=4 AND ProductID=51) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (4, 51);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=10 AND ProductID=40) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (10, 40);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=4 AND ProductID=43) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (4, 43);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=9 AND ProductID=49) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (9, 49);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=5 AND ProductID=47) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (5, 47);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=4 AND ProductID=13) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (4, 13);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=10 AND ProductID=25) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (10, 25);
IF NOT EXISTS (SELECT 1 FROM dbo.UserFavorites WHERE UserID=3 AND ProductID=3) INSERT INTO dbo.UserFavorites(UserID, ProductID) VALUES (3, 3);
GO
-- Thêm địa chỉ mẫu cho một số user
INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, City, Country, IsDefault) 
VALUES (5, N'Customer 1', N'0912300001', N'123 Đường ABC', N'Hà Nội', N'Việt Nam', 1);
GO

--hàm và trigger hỗ trợ


IF OBJECT_ID('dbo.fn_GetUnreadNotificationCount', 'FN') IS NOT NULL
    DROP FUNCTION dbo.fn_GetUnreadNotificationCount;
GO
CREATE FUNCTION dbo.fn_GetUnreadNotificationCount(@UserID INT)
RETURNS INT
AS
BEGIN
    DECLARE @Count INT;
    SELECT @Count = COUNT(*)
    FROM Notifications
    WHERE UserID = @UserID AND IsRead = 0;
    RETURN @Count;
END;
GO

-- TRIGGER: Tự động thêm thông báo khi trạng thái đơn hàng thay đổi
IF OBJECT_ID('dbo.trg_Orders_StatusChange_Notification', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_Orders_StatusChange_Notification;
GO
CREATE TRIGGER dbo.trg_Orders_StatusChange_Notification
ON Orders
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO Notifications (UserID, Content, CreatedAt, IsRead)
    SELECT i.UserID,
           CONCAT('Trạng thái đơn hàng #', i.OrderID, ' đã đổi sang ', i.OrderStatus),
           GETDATE(),
           0
    FROM inserted i
    JOIN deleted d ON i.OrderID = d.OrderID
    WHERE i.OrderStatus <> d.OrderStatus;
END;
GO

-- 💬 PROCEDURE: Gửi thông báo tùy ý đến người dùng
IF OBJECT_ID('dbo.sp_CreateOrderNotification', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_CreateOrderNotification;
GO
CREATE PROCEDURE dbo.sp_CreateOrderNotification
    @UserID INT,
    @OrderID INT,
    @Message NVARCHAR(255)
AS
BEGIN
    INSERT INTO Notifications (UserID, Content, CreatedAt, IsRead)
    VALUES (@UserID, CONCAT(N'Đơn hàng #', @OrderID, ': ', @Message), GETDATE(), 0);
END;
GO

-- 💸 TRIGGER: Cập nhật tồn kho sau khi thêm OrderItems
IF OBJECT_ID('dbo.trg_UpdateProductStock_AfterOrder', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_UpdateProductStock_AfterOrder;
GO
CREATE TRIGGER dbo.trg_UpdateProductStock_AfterOrder
ON OrderItems
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p
    SET p.Stock = p.Stock - i.Quantity
    FROM Products p
    JOIN inserted i ON p.ProductID = i.ProductID;
END;
GO

-- 📈 FUNCTION: Tính doanh thu theo tháng
IF OBJECT_ID('dbo.fn_GetMonthlyRevenue', 'FN') IS NOT NULL
    DROP FUNCTION dbo.fn_GetMonthlyRevenue;
GO
CREATE FUNCTION dbo.fn_GetMonthlyRevenue(@Year INT)
RETURNS TABLE
AS
RETURN (
    SELECT 
        MONTH(o.OrderDate) AS MonthNum,
        SUM(oi.Quantity * oi.UnitPrice) AS TotalRevenue
    FROM Orders o
    JOIN OrderItems oi ON o.OrderID = oi.OrderID
    WHERE YEAR(o.OrderDate) = @Year
    GROUP BY MONTH(o.OrderDate)
);
GO

-- 💡 TRIGGER: Khi thêm sản phẩm mới -> tự động thêm log vào bảng ProductViews
IF OBJECT_ID('dbo.trg_Product_AfterInsert_Log', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_Product_AfterInsert_Log;
GO
CREATE TRIGGER dbo.trg_Product_AfterInsert_Log
ON Products
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO ProductViews (ProductID, UserID, ViewedAt)
    SELECT i.ProductID, NULL, GETDATE() FROM inserted i;
END;
GO

PRINT 'All triggers, functions, and procedures have been created successfully!';
GO

-- ========== THÊM DỮ LIỆU ĐỊA CHỈ MẪU ==========
-- Thêm địa chỉ mẫu cho các user (đặc biệt là customer users)
-- Đảm bảo mỗi user có ít nhất 1 địa chỉ, một số user có địa chỉ mặc định

-- Địa chỉ cho Admin (UserID = 1)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 1) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (1, N'Nguyen Van A', N'0900000001', N'123 Đường Láng', N'Phường Láng Thượng', N'Hà Nội', N'Đống Đa', N'Láng Thượng', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Manager (UserID = 2)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 2) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (2, N'Tran Thi B', N'0900000002', N'456 Đường Giải Phóng', N'Phường Phương Mai', N'Hà Nội', N'Đống Đa', N'Phương Mai', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Staff (UserID = 3, 4)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 3) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (3, N'Le Van C', N'0900000003', N'789 Đường Lê Duẩn', N'Phường Bạch Đằng', N'Hà Nội', N'Hai Bà Trưng', N'Bạch Đằng', N'Việt Nam', N'100000', 1);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 4) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (4, N'Pham Thi D', N'0900000004', N'321 Đường Trần Phú', N'Phường Văn Quán', N'Hà Đông', N'Hà Đông', N'Văn Quán', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 1 (UserID = 5)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 5) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (5, N'Customer 1', N'0912300001', N'Số 10 Ngõ 123', N'Phường Cầu Giấy', N'Hà Nội', N'Cầu Giấy', N'Cầu Giấy', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 2 (UserID = 6) - 2 địa chỉ
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 6 AND IsDefault = 1) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (6, N'Customer 2', N'0912300002', N'Số 20 Đường Nguyễn Văn Cừ', N'Phường Ngọc Lâm', N'Hà Nội', N'Long Biên', N'Ngọc Lâm', N'Việt Nam', N'100000', 1);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 6 AND IsDefault = 0) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (6, N'Customer 2', N'0912300002', N'Số 25 Đường Lạc Long Quân', N'Phường Xuân La', N'Hà Nội', N'Tây Hồ', N'Xuân La', N'Việt Nam', N'100000', 0);
GO

-- Địa chỉ cho Customer 3 (UserID = 7)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 7) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (7, N'Customer 3', N'0912300003', N'Số 30 Đường Hoàng Quốc Việt', N'Phường Nghĩa Đô', N'Hà Nội', N'Cầu Giấy', N'Nghĩa Đô', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 4 (UserID = 8)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 8) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (8, N'Customer 4', N'0912300004', N'Số 40 Đường Phạm Văn Đồng', N'Phường Cổ Nhuế', N'Hà Nội', N'Bắc Từ Liêm', N'Cổ Nhuế', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 5 (UserID = 9) - 2 địa chỉ
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 9 AND IsDefault = 1) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (9, N'Customer 5', N'0912300005', N'Số 50 Đường Láng Hạ', N'Phường Láng Hạ', N'Hà Nội', N'Đống Đa', N'Láng Hạ', N'Việt Nam', N'100000', 1);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 9 AND IsDefault = 0) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (9, N'Customer 5', N'0912300005', N'Số 55 Đường Kim Mã', N'Phường Kim Mã', N'Hà Nội', N'Ba Đình', N'Kim Mã', N'Việt Nam', N'100000', 0);
GO

-- Địa chỉ cho Customer 6 (UserID = 10)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 10) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (10, N'Customer 6', N'0912300006', N'Số 60 Đường Trường Chinh', N'Phường Khương Trung', N'Hà Nội', N'Thanh Xuân', N'Khương Trung', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 7 (UserID = 11)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 11) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (11, N'Customer 7', N'0912300007', N'Số 70 Đường Lê Đức Thọ', N'Phường Mỹ Đình', N'Hà Nội', N'Nam Từ Liêm', N'Mỹ Đình', N'Việt Nam', N'100000', 1);
GO

-- Địa chỉ cho Customer 8 (UserID = 12)
IF NOT EXISTS (SELECT 1 FROM dbo.Addresses WHERE UserID = 12) 
    INSERT INTO dbo.Addresses(UserID, FullName, Phone, Line1, Line2, City, District, Ward, Country, PostalCode, IsDefault) 
    VALUES (12, N'Customer 8', N'0912300008', N'Số 80 Đường Nguyễn Trãi', N'Phường Thanh Xuân Bắc', N'Hà Nội', N'Thanh Xuân', N'Thanh Xuân Bắc', N'Việt Nam', N'100000', 1);
GO

PRINT 'Sample addresses have been added successfully!';
GO