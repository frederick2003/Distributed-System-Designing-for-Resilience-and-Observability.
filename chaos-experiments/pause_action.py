import time
from logzero import logger


def pause(seconds):
    logger.info(f"Pausing for {seconds} seconds to allow system recovery...")
    time.sleep(seconds)
    logger.info("Resuming chaos experiment.")
