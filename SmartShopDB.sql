
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
DROP TABLE IF EXISTS dbo.CartItems;
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

-- CartItems: Lưu giỏ hàng của người dùng
CREATE TABLE dbo.CartItems (
  CartItemID INT IDENTITY(1,1) PRIMARY KEY,
  UserID INT NOT NULL,
  ProductID INT NOT NULL,
  Quantity INT NOT NULL CONSTRAINT DF_CartItems_Quantity DEFAULT 1,
  AddedDate DATETIME2 NOT NULL CONSTRAINT DF_CartItems_AddedDate DEFAULT SYSDATETIME(),
  CONSTRAINT FK_CartItems_Users FOREIGN KEY(UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE,
  CONSTRAINT FK_CartItems_Products FOREIGN KEY(ProductID) REFERENCES dbo.Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT UQ_CartItems_UserProduct UNIQUE(UserID, ProductID) -- Mỗi user chỉ có 1 record cho mỗi product
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

-- Indexes
CREATE INDEX IX_Products_Category ON dbo.Products(CategoryID);
GO
CREATE INDEX IX_OrderItems_Order ON dbo.OrderItems(OrderID);
GO

-- Index cho CartItems
CREATE INDEX IX_CartItems_User ON dbo.CartItems(UserID);
CREATE INDEX IX_CartItems_Product ON dbo.CartItems(ProductID);
GO
CREATE INDEX IX_Payments_Order ON dbo.Payments(OrderID);
GO
CREATE INDEX IX_ProductViews_Product ON dbo.ProductViews(ProductID);
GO
CREATE INDEX IX_SocialShares_Product ON dbo.SocialShares(ProductID);
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
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'smartshop686868@gmail.com') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Admin', N'admin@smartshop.local', N'hash_admin', N'0833347220', 1);
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
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE Email=N'dinosaurlu11@gmail.com') INSERT INTO dbo.Users(FullName, Email, PasswordHash, Phone, RoleID) VALUES (N'Dino', N'dinosaurlu11@gmail.com', N'hash_dinosaur', N'0912300008', 4);
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
-- Products - Real Products with Real Image URLs
-- Category 1: Điện thoại (Smartphones)
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (1, N'iPhone 15 Pro Max 256GB', N'iphone-15-pro-max-256gb', N'iPhone 15 Pro Max với chip A17 Pro, màn hình 6.7 inch Super Retina XDR, camera 48MP, pin khủng', 28990000, N'Xanh Titan', 68, N'InStock', N'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (1, N'Samsung Galaxy S24 Ultra 512GB', N'samsung-galaxy-s24-ultra-512gb', N'Galaxy S24 Ultra với S Pen, camera 200MP, chip Snapdragon 8 Gen 3, màn hình Dynamic AMOLED 2X 6.8 inch', 26990000, N'Đen Titan', 78, N'InStock', N'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'Xiaomi 14 Pro 256GB', N'xiaomi-14-pro-256gb', N'Xiaomi 14 Pro với chip Snapdragon 8 Gen 3, camera Leica 50MP, màn hình AMOLED 6.73 inch, sạc nhanh 120W', 19990000, N'Đen', 23, N'InStock', N'https://images.unsplash.com/photo-1601784551446-20c9e07cdbdb?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'OPPO Find X7 Ultra 512GB', N'oppo-find-x7-ultra-512gb', N'OPPO Find X7 Ultra với camera 50MP Hasselblad, chip Snapdragon 8 Gen 3, màn hình LTPO AMOLED 6.82 inch', 24990000, N'Xanh', 51, N'InStock', N'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'vivo X100 Pro 512GB', N'vivo-x100-pro-512gb', N'vivo X100 Pro với camera ZEISS 50MP, chip MediaTek Dimensity 9300, màn hình AMOLED 6.78 inch', 22990000, N'Xanh', 56, N'InStock', N'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (1, N'iPhone 14 128GB', N'iphone-14-128gb', N'iPhone 14 với chip A15 Bionic, camera kép 12MP, màn hình Super Retina XDR 6.1 inch', 18990000, N'Tím', 39, N'InStock', N'https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=500');
-- Category 2: Laptop
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (2, N'MacBook Pro 14 inch M3 Pro 512GB', N'macbook-pro-14-m3-pro-512gb', N'MacBook Pro 14 inch với chip Apple M3 Pro, RAM 18GB, SSD 512GB, màn hình Liquid Retina XDR', 54900000, N'Xám Space', 94, N'InStock', N'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'ASUS ROG Strix G16 2024', N'asus-rog-strix-g16-2024', N'Laptop gaming ASUS ROG Strix G16 với CPU Intel Core i9-14900HX, GPU RTX 4070, RAM 16GB, SSD 1TB', 42900000, N'Đen', 76, N'InStock', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Dell XPS 15 9530', N'dell-xps-15-9530', N'Dell XPS 15 với CPU Intel Core i7-13700H, GPU RTX 4050, RAM 16GB, SSD 512GB, màn hình 15.6 inch OLED', 47900000, N'Bạc', 79, N'InStock', N'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'HP Spectre x360 14', N'hp-spectre-x360-14', N'HP Spectre x360 14 với CPU Intel Core i7-1355U, RAM 16GB, SSD 1TB, màn hình OLED 14 inch touchscreen', 39900000, N'Xanh Navy', 85, N'InStock', N'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'Lenovo ThinkPad X1 Carbon Gen 11', N'lenovo-thinkpad-x1-carbon-gen11', N'ThinkPad X1 Carbon với CPU Intel Core i7-1355U, RAM 16GB, SSD 512GB, màn hình 14 inch 2.8K', 38900000, N'Đen', 20, N'InStock', N'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (2, N'ASUS Zenbook 14 OLED', N'asus-zenbook-14-oled', N'ASUS Zenbook 14 với CPU AMD Ryzen 7 7735U, RAM 16GB, SSD 512GB, màn hình OLED 14 inch 2.8K', 24900000, N'Xanh', 26, N'InStock', N'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500');
-- Category 3: Tablet
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (3, N'iPad Pro 12.9 inch M2 256GB', N'ipad-pro-12.9-m2-256gb', N'iPad Pro 12.9 inch với chip Apple M2, RAM 8GB, màn hình Liquid Retina XDR, hỗ trợ Apple Pencil 2', 29900000, N'Xám Space', 36, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Samsung Galaxy Tab S9 Ultra 512GB', N'samsung-galaxy-tab-s9-ultra-512gb', N'Galaxy Tab S9 Ultra với chip Snapdragon 8 Gen 2, RAM 12GB, màn hình Dynamic AMOLED 2X 14.6 inch', 28900000, N'Bạc', 73, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'iPad Air 11 inch M2 256GB', N'ipad-air-11-m2-256gb', N'iPad Air 11 inch với chip Apple M2, RAM 8GB, màn hình Liquid Retina, hỗ trợ Apple Pencil 2', 19900000, N'Tím', 63, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Xiaomi Pad 6 Pro 256GB', N'xiaomi-pad-6-pro-256gb', N'Xiaomi Pad 6 Pro với chip Snapdragon 8+ Gen 1, RAM 12GB, màn hình LCD 11 inch 2.8K 144Hz', 12900000, N'Xanh', 39, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Lenovo Tab P12 Pro', N'lenovo-tab-p12-pro', N'Lenovo Tab P12 Pro với chip Snapdragon 870, RAM 8GB, màn hình OLED 12.6 inch, hỗ trợ bút cảm ứng', 15900000, N'Xám', 73, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (3, N'Samsung Galaxy Tab S9 FE+', N'samsung-galaxy-tab-s9-fe-plus', N'Galaxy Tab S9 FE+ với chip Exynos 1380, RAM 8GB, màn hình LCD 12.4 inch, hỗ trợ S Pen', 13900000, N'Đen', 19, N'InStock', N'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500');
-- Category 4: Phụ kiện
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Logitech MX Master 3S', N'logitech-mx-master-3s', N'Chuột không dây Logitech MX Master 3S với sensor 8K DPI, pin sạc, kết nối Bluetooth và USB receiver', 2590000, N'Xám', 33, N'InStock', N'https://images.unsplash.com/photo-1527814050087-3793815479db?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Apple Magic Keyboard', N'apple-magic-keyboard', N'Bàn phím không dây Apple Magic Keyboard với pin sạc, kết nối Bluetooth, thiết kế siêu mỏng', 3290000, N'Trắng', 23, N'InStock', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Anker Power Bank 20000mAh', N'anker-power-bank-20000mah', N'Sạc dự phòng Anker PowerCore 20000mAh với sạc nhanh 20W, 2 cổng USB-A và 1 cổng USB-C', 1290000, N'Đen', 67, N'InStock', N'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c7?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Spigen iPhone 15 Pro Case', N'spigen-iphone-15-pro-case', N'Ốp lưng Spigen cho iPhone 15 Pro với bảo vệ chống sốc, thiết kế trong suốt, hỗ trợ MagSafe', 890000, N'Trong suốt', 35, N'InStock', N'https://images.unsplash.com/photo-1556656793-08538906a9f8?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'Belkin BoostCharge Pro 3-in-1', N'belkin-boostcharge-pro-3in1', N'Đế sạc không dây Belkin BoostCharge Pro 3-in-1 cho iPhone, AirPods và Apple Watch', 5990000, N'Trắng', 81, N'InStock', N'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c7?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (4, N'HyperX Cloud Alpha Wireless', N'hyperx-cloud-alpha-wireless', N'Tai nghe gaming HyperX Cloud Alpha Wireless với pin 300 giờ, âm thanh DTS, micrô rời', 4990000, N'Đỏ Đen', 91, N'InStock', N'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500');
-- Category 5: Âm thanh
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (5, N'Sony WH-1000XM5', N'sony-wh-1000xm5', N'Tai nghe chống ồn Sony WH-1000XM5 với công nghệ ANC, pin 30 giờ, sạc nhanh 3 phút = 3 giờ', 8990000, N'Đen', 18, N'InStock', N'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'AirPods Pro 2 USB-C', N'airpods-pro-2-usb-c', N'AirPods Pro 2 với chip H2, chống ồn chủ động, pin 6 giờ + 24 giờ với hộp sạc, cổng USB-C', 6990000, N'Trắng', 44, N'InStock', N'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'JBL Flip 6', N'jbl-flip-6', N'Loa Bluetooth JBL Flip 6 với công suất 30W, chống nước IPX7, pin 12 giờ, âm bass mạnh', 3990000, N'Xanh', 54, N'InStock', N'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Soundcore Liberty 4 NC', N'soundcore-liberty-4-nc', N'Tai nghe true wireless Soundcore Liberty 4 NC với ANC, pin 10 giờ + 50 giờ với hộp sạc', 3490000, N'Đen', 92, N'InStock', N'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Samsung Galaxy Buds2 Pro', N'samsung-galaxy-buds2-pro', N'Galaxy Buds2 Pro với ANC thông minh, pin 8 giờ + 29 giờ, âm thanh 360 Audio, chống nước IPX7', 4490000, N'Tím', 64, N'InStock', N'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (5, N'Bose QuietComfort Earbuds II', N'bose-quietcomfort-earbuds-ii', N'Bose QuietComfort Earbuds II với ANC tùy chỉnh, pin 6 giờ + 18 giờ, âm thanh sống động', 7990000, N'Trắng', 73, N'InStock', N'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=500');
-- Category 6: Smartwatch
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (6, N'Apple Watch Ultra 2', N'apple-watch-ultra-2', N'Apple Watch Ultra 2 với chip S9 SiP, màn hình 49mm, pin 36 giờ, chống nước 100m, GPS kép', 19900000, N'Cam Titan', 53, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Samsung Galaxy Watch6 Classic', N'samsung-galaxy-watch6-classic', N'Galaxy Watch6 Classic với màn hình 47mm, vòng bezel xoay, pin 40 giờ, đo huyết áp và ECG', 8990000, N'Đen', 29, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Garmin Forerunner 965', N'garmin-forerunner-965', N'Garmin Forerunner 965 với màn hình AMOLED 1.4 inch, pin 23 ngày, GPS chính xác, đo VO2 max', 12900000, N'Đen', 59, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Apple Watch Series 9 GPS', N'apple-watch-series-9-gps', N'Apple Watch Series 9 với chip S9 SiP, màn hình 45mm, pin 18 giờ, đo SpO2 và ECG', 12900000, N'Hồng', 84, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Xiaomi Watch S3', N'xiaomi-watch-s3', N'Xiaomi Watch S3 với màn hình AMOLED 1.43 inch, pin 15 ngày, hơn 150 chế độ thể thao', 3490000, N'Đen', 7, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (6, N'Huawei Watch GT 4', N'huawei-watch-gt-4', N'Huawei Watch GT 4 với màn hình AMOLED 1.43 inch, pin 14 ngày, hơn 100 chế độ thể thao, đo SpO2', 5990000, N'Bạc', 76, N'InStock', N'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500');
-- Category 7: Thiết bị mạng
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'TP-Link Archer AX6000', N'tp-link-archer-ax6000', N'Router Wi-Fi 6 TP-Link Archer AX6000 với tốc độ 5952 Mbps, 8 ăng-ten, hỗ trợ MU-MIMO', 4990000, N'Đen', 23, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'ASUS RT-AX86U Pro', N'asus-rt-ax86u-pro', N'Router gaming ASUS RT-AX86U Pro với Wi-Fi 6, tốc độ 5700 Mbps, hỗ trợ AiMesh, Game Boost', 5990000, N'Đen', 87, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Ubiquiti UniFi Dream Machine', N'ubiquiti-unifi-dream-machine', N'Ubiquiti UniFi Dream Machine với bộ điều khiển UniFi, router, switch và gateway tích hợp', 8990000, N'Trắng', 68, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Netgear Nighthawk RAX50', N'netgear-nighthawk-rax50', N'Router Wi-Fi 6 Netgear Nighthawk RAX50 với tốc độ 5400 Mbps, hỗ trợ OFDMA và MU-MIMO', 5490000, N'Đen', 26, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Linksys Velop MX4200', N'linksys-velop-mx4200', N'Hệ thống mesh Wi-Fi 6 Linksys Velop MX4200 với 3 node, phủ sóng lên tới 6000 sq ft', 7990000, N'Trắng', 20, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (7, N'Google Nest Wifi Pro', N'google-nest-wifi-pro', N'Router mesh Wi-Fi 6E Google Nest Wifi Pro với 3 node, hỗ trợ Matter và Thread', 8990000, N'Trắng', 61, N'InStock', N'https://images.unsplash.com/photo-1609081219090-a6d81d3085bf?w=500');
-- Category 8: Gaming Gear
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (8, N'Razer DeathAdder V3 Pro', N'razer-deathadder-v3-pro', N'Chuột gaming không dây Razer DeathAdder V3 Pro với sensor Focus Pro 30K, pin 90 giờ', 3990000, N'Đen', 68, N'InStock', N'https://images.unsplash.com/photo-1527814050087-3793815479db?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Corsair K70 RGB TKL', N'corsair-k70-rgb-tkl', N'Bàn phím gaming cơ Corsair K70 RGB TKL với switch Cherry MX, đèn RGB, thiết kế TKL', 4490000, N'Đen', 72, N'InStock', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'SteelSeries Arctis Nova Pro Wireless', N'steelseries-arctis-nova-pro-wireless', N'Tai nghe gaming SteelSeries Arctis Nova Pro Wireless với ANC, pin 44 giờ, base station', 8990000, N'Đen', 37, N'InStock', N'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'Logitech G Pro X Superlight 2', N'logitech-g-pro-x-superlight-2', N'Chuột gaming siêu nhẹ Logitech G Pro X Superlight 2 với sensor HERO 2, chỉ 60g', 3490000, N'Trắng', 76, N'InStock', N'https://images.unsplash.com/photo-1527814050087-3793815479db?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'HyperX Alloy Elite 2', N'hyperx-alloy-elite-2', N'Bàn phím gaming HyperX Alloy Elite 2 với switch HyperX Red, đèn RGB, volume wheel', 3290000, N'Đen', 86, N'InStock', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (8, N'ASUS ROG Strix Scope II 96', N'asus-rog-strix-scope-ii-96', N'Bàn phím gaming ASUS ROG Strix Scope II 96 với switch ROG NX, layout 96%, đèn RGB', 4990000, N'Đen', 93, N'InStock', N'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500');
-- Category 9: TV & Monitor
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl, IsSpecial) VALUES (9, N'Samsung 55 inch QLED 4K Q80C', N'samsung-55-qled-4k-q80c', N'TV Samsung 55 inch QLED 4K Q80C với Quantum HDR, Smart TV Tizen, hỗ trợ HDR10+', 19900000, N'Đen', 21, N'InStock', N'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500', 1);
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'LG 65 inch OLED 4K C3', N'lg-65-oled-4k-c3', N'TV LG 65 inch OLED 4K C3 với công nghệ OLED, hỗ trợ Dolby Vision và Dolby Atmos', 34900000, N'Đen', 65, N'InStock', N'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'ASUS ROG Swift PG32UCDM', N'asus-rog-swift-pg32ucdm', N'Màn hình gaming ASUS ROG Swift PG32UCDM 32 inch 4K OLED 240Hz, G-Sync, HDR10', 24900000, N'Đen', 34, N'InStock', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'Dell UltraSharp U3223QE', N'dell-ultrasharp-u3223qe', N'Màn hình Dell UltraSharp U3223QE 32 inch 4K IPS, USB-C 90W, sRGB 99%, thiết kế văn phòng', 14900000, N'Đen', 7, N'InStock', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'Samsung Odyssey G9 49 inch', N'samsung-odyssey-g9-49inch', N'Màn hình cong gaming Samsung Odyssey G9 49 inch QLED 240Hz, G-Sync, HDR1000', 22900000, N'Xanh', 80, N'InStock', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (9, N'LG UltraGear 27GR95QE', N'lg-ultragear-27gr95qe', N'Màn hình gaming LG UltraGear 27GR95QE 27 inch QHD OLED 240Hz, G-Sync, HDR10', 17900000, N'Đen', 36, N'InStock', N'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500');
-- Category 10: Nhà thông minh
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Google Nest Hub Max', N'google-nest-hub-max', N'Màn hình thông minh Google Nest Hub Max 10 inch, camera, điều khiển nhà thông minh', 8990000, N'Trắng', 31, N'InStock', N'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Ring Video Doorbell Pro 2', N'ring-video-doorbell-pro-2', N'Chuông cửa thông minh Ring Video Doorbell Pro 2 với camera 1536p, night vision, hai chiều', 7990000, N'Đen', 53, N'InStock', N'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Philips Hue Starter Kit', N'philips-hue-starter-kit', N'Bộ đèn thông minh Philips Hue Starter Kit với 3 bóng đèn màu, hub, điều khiển qua app', 3990000, N'Trắng', 22, N'InStock', N'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Amazon Echo Dot 5th Gen', N'amazon-echo-dot-5th-gen', N'Loa thông minh Amazon Echo Dot 5th Gen với Alexa, điều khiển nhà thông minh', 1990000, N'Xanh', 12, N'InStock', N'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Xiaomi Smart Camera C300', N'xiaomi-smart-camera-c300', N'Camera an ninh Xiaomi Smart Camera C300 với độ phân giải 2K, night vision, phát hiện chuyển động', 1290000, N'Trắng', 29, N'InStock', N'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500');
INSERT INTO dbo.Products(CategoryID, ProductName, Slug, Description, Price, Color, Stock, StockStatus, ImageUrl) VALUES (10, N'Aqara Smart Home Hub M2', N'aqara-smart-home-hub-m2', N'Bộ điều khiển trung tâm Aqara Smart Home Hub M2 với hỗ trợ Zigbee 3.0, Matter, HomeKit', 1990000, N'Trắng', 18, N'InStock', N'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500');
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
-- ProductViews / SocialShares
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