# Deploy Odoo‑Adani on AWS (Free‑tier)

## Overview
This guide shows how to run the **Spring‑Boot backend** in a Docker container on **AWS Elastic Container Service (ECS) Fargate** and serve the **React frontend** from **Amazon S3 + CloudFront** while using an **AWS RDS MySQL** instance that is covered by the 12‑month free tier.

---
### 1️⃣ Prerequisites
- AWS account (free tier) – you need a credit‑card for verification.
- AWS CLI installed and configured (`aws configure`).
- Docker installed locally.
- GitHub repository `https://github.com/DSheth2004/Odoo-Adani.git` (already exists).
- **AWS free tier limits**:
  - 750 hrs / month of **ECS Fargate** (enough for one service).
  - 1 t2.micro RDS instance (MySQL) for 12 months – 20 GB SSD.
  - 5 GB S3 storage (enough for the React build).

---
### 2️⃣ Create the MySQL RDS instance (Free tier)
```bash
# Create a subnet group (use default VPC subnets if you prefer)
aws rds create-db-subnet-group \
  --db-subnet-group-name maintsync-subnet \
  --db-subnet-group-description "Subnet group for MaintSync" \
  --subnet-ids $(aws ec2 describe-subnets --query "Subnets[?VpcId!='null'].SubnetId" --output text)

# Create the free‑tier MySQL instance (db.t2.micro)
aws rds create-db-instance \
  --db-instance-identifier maintsync-db \
  --db-instance-class db.t2.micro \
  --engine mysql \
  --allocated-storage 20 \
  --master-username admin \
  --master-user-password <YOUR_PASSWORD> \
  --db-subnet-group-name maintsync-subnet \
  --backup-retention-period 7 \
  --no-multi-az \
  --publicly-accessible true \
  --tags Key=Project,Value=OdooAdani
```
After creation, fetch the endpoint:
```bash
aws rds describe-db-instances \
  --db-instance-identifier maintsync-db \
  --query "DBInstances[0].Endpoint.Address" \
  --output text
```
**Add the endpoint to `.env`** (see below).

---
### 3️⃣ Build & push the Docker image
```bash
# Clone the repo if not already local
git clone https://github.com/DSheth2004/Odoo-Adani.git
cd Odoo-Adani/server-springboot

# Build the image (replace <ACCOUNT_ID> with your AWS account ID)
IMAGE_NAME=<ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/maintsync-backend:latest

# Authenticate Docker to ECR
eaws ecr get-login-password --region <REGION> | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com

# Create ECR repository (if not exists)
aws ecr create-repository --repository-name maintsync-backend

# Build and push
docker build -t $IMAGE_NAME .
docker push $IMAGE_NAME
```

---
### 4️⃣ Deploy backend on ECS Fargate
```bash
# Create a task definition (JSON simplified – you can also use the console)
cat > task-def.json <<'EOF'
{
  "family": "maintsync-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "${IMAGE_NAME}",
      "essential": true,
      "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
      "environment": [
        {"name":"DB_HOST","value":"<RDS_ENDPOINT>"},
        {"name":"DB_PORT","value":"3306"},
        {"name":"DB_NAME","value":"maintenance_db"},
        {"name":"DB_USER","value":"admin"},
        {"name":"DB_PASS","value":"<YOUR_PASSWORD>"},
        {"name":"JWT_SECRET","value":"<RANDOM_64_BYTE_BASE64>"},
        {"name":"SERVER_PORT","value":"8080"},
        {"name":"CLIENT_ORIGIN","value":"https://<YOUR_CLOUDFRONT_DOMAIN>"}
      ]
    }
  ]
}
EOF

# Register task definition
aws ecs register-task-definition --cli-input-json file://task-def.json

# Create a Fargate service (uses the default VPC and a public subnet)
aws ecs create-service \
  --cluster default \
  --service-name maintsync-backend \
  --task-definition maintsync-backend \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=$(aws ec2 describe-subnets --filters Name=default-for-az,Values=true --query 'Subnets[*].SubnetId' --output text),assignPublicIp=ENABLED}"
```
The service will be reachable via a **public load balancer** automatically created by the console; you can also create an **Application Load Balancer (ALB)** manually and point its target group to the service.

---
### 5️⃣ Deploy the React frontend to S3 + CloudFront
```bash
# Build the frontend locally
cd ../../client
npm install && npm run build    # output goes to ./dist

# Create an S3 bucket (must be globally unique)
BUCKET_NAME=maintsync-frontend-$(aws sts get-caller-identity --query Account --output text)
aws s3api create-bucket --bucket $BUCKET_NAME --region <REGION> --acl public-read

# Sync the build folder to S3
aws s3 sync dist/ s3://$BUCKET_NAME/ --delete

# Create a CloudFront distribution (simplified)
aws cloudfront create-distribution \
  --origin-domain-name $BUCKET_NAME.s3.amazonaws.com \
  --default-root-object index.html
```
Copy the **CloudFront domain name** (e.g., `d1234abcd.cloudfront.net`) and set it as `CLIENT_ORIGIN` in the ECS task definition (or update the env var via the console).

---
### 6️⃣ Update `.env` (project root) for local testing (optional)
```text
DB_HOST=<RDS_ENDPOINT>
DB_PORT=3306
DB_NAME=maintenance_db
DB_USER=admin
DB_PASS=<YOUR_PASSWORD>
JWT_SECRET=<RANDOM_64_BYTE_BASE64>
SERVER_PORT=8080
CLIENT_ORIGIN=https://<CLOUDFRONT_DOMAIN>
```
Your local `run_schema.sh` script can now initialise the remote DB:
```bash
cd server-springboot
./scripts/run_schema.sh
```

---
### 7️⃣ Verify the deployment
- **Backend health:** `curl https://<ALB_OR_PUBLIC_IP>/actuator/health` → `{ "status":"UP", "db":"UP" }`
- **Frontend:** Open the CloudFront URL, log in, and ensure data loads without CORS errors.
- **Logs:** Use **CloudWatch** for ECS task logs and **RDS** logs for DB issues.

---
### 8️⃣ Cost monitoring (stay within free tier)
- Set **Billing alarms** in the AWS console (e.g., alert at $0.50).
- Keep the ECS service at **1 task** and the RDS instance **stopped** when not in use (`aws rds stop-db-instance`).
- Delete the S3 bucket or set lifecycle rules after the demo.

---
## TL;DR Commands (copy‑paste)
```bash
# 1. Create RDS (free tier)
aws rds create-db-instance --db-instance-identifier maintsync-db --db-instance-class db.t2.micro \
  --engine mysql --allocated-storage 20 --master-username admin --master-user-password <pwd> \
  --publicly-accessible true --no-multi-az

# 2. Build & push Docker image
aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com
aws ecr create-repository --repository-name maintsync-backend
docker build -t <account>.dkr.ecr.<region>.amazonaws.com/maintsync-backend:latest .
docker push <account>.dkr.ecr.<region>.amazonaws.com/maintsync-backend:latest

# 3. Deploy to ECS (Fargate)
# (use the task‑def.json shown above, then register & create‑service)

# 4. Deploy frontend
npm install && npm run build
aws s3 sync dist/ s3://<bucket>/ --delete
aws cloudfront create-distribution --origin-domain-name <bucket>.s3.amazonaws.com --default-root-object index.html
```
---
### References
- AWS Free Tier details: https://aws.amazon.com/free/
- ECS Fargate tutorial: https://docs.aws.amazon.com/AmazonECS/latest/developerguide/fargate-getting-started.html
- RDS MySQL free tier: https://aws.amazon.com/rds/mysql/free/
- S3 + CloudFront static‑site hosting: https://docs.aws.amazon.com/AmazonS3/latest/userguide/website-hosting-cloudfront.html

Feel free to ask if you need any of these steps automated (e.g., a CloudFormation template) or if you run into permission issues.
