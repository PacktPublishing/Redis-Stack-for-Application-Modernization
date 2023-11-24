import os
import random
import redis
from datetime import date, timedelta
from dotenv import load_dotenv

REDIS_HOST = 'REDIS_HOST'
REDIS_PORT = 'REDIS_PORT'
REDIS_UID = 'REDIS_UID'
REDIS_PWD = 'REDIS_PWD'

load_dotenv()

client = redis.Redis(host=os.environ.get(REDIS_HOST),
                     port=os.environ.get(REDIS_PORT),
                     username=os.environ.get(REDIS_UID),
                     password=os.environ.get(REDIS_PWD))

time_series_key = 'mortensi.com'

start_date = date(2023, 1, 1)
end_date = date(2023, 4, 30)
delta = timedelta(days=1)

client.execute_command('DEL', time_series_key)

current_date = start_date
while current_date <= end_date:
    timestamp = int(current_date.strftime('%s')) * 1000
    value = random.randint(1, 1000)
    client.execute_command('TS.ADD', time_series_key, timestamp, value)
    current_date += delta
print("Site https://mortensi.com completed")
